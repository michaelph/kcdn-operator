package br.rnp.kcdnoperator.controllers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.rnp.kcdnoperator.crs.kbox.Kbox;
import br.rnp.kcdnoperator.crs.kbox.KboxDoneable;
import br.rnp.kcdnoperator.crs.kbox.KboxList;
import br.rnp.kcdnoperator.crs.kistribution.Kistribution;
import br.rnp.kcdnoperator.crs.kistribution.KistributionDoneable;
import br.rnp.kcdnoperator.crs.kistribution.KistributionList;
import br.rnp.kcdnoperator.crs.kopology.Kopology;
import br.rnp.kcdnoperator.crs.kopology.KopologyDoneable;
import br.rnp.kcdnoperator.crs.kopology.KopologyList;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodBuilder;
import io.fabric8.kubernetes.api.model.PodFluent.SpecNested;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.cache.Cache;
import io.fabric8.kubernetes.client.informers.cache.Lister;

public class KcdnController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KcdnController.class);
    private KubernetesClient kubernetesClient;
    // Clients
    private MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> k8sKopologyClient;
    private MixedOperation<Kistribution, KistributionList, KistributionDoneable, Resource<Kistribution, KistributionDoneable>> k8sKistributionClient;
    private MixedOperation<Kbox, KboxList, KboxDoneable, Resource<Kbox, KboxDoneable>> k8sKboxClient;
    // Informers
    private SharedIndexInformer<Kopology> kopologyInformer;
    private SharedIndexInformer<Kistribution> kistributionInformer;
    private SharedIndexInformer<Kbox> kboxInformer;
    // Listers
    private Lister<Kopology> kopologyLister;
    private Lister<Kistribution> kistributionLister;
    private Lister<Kbox> kboxLister;
    // Queue
    private BlockingQueue<String> workqueue;

    public KcdnController(KubernetesClient kubernetesClient,
            MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> k8sKopologyClient,
            MixedOperation<Kistribution, KistributionList, KistributionDoneable, Resource<Kistribution, KistributionDoneable>> k8sKistributionClient,
            MixedOperation<Kbox, KboxList, KboxDoneable, Resource<Kbox, KboxDoneable>> k8sKboxClient,
            SharedIndexInformer<Kopology> kopologyInformer, SharedIndexInformer<Kistribution> kistributionInformer,
            SharedIndexInformer<Kbox> kboxInformer) {
        // Clients
        this.kubernetesClient = kubernetesClient;
        this.k8sKopologyClient = k8sKopologyClient;
        this.k8sKistributionClient = k8sKistributionClient;
        this.k8sKboxClient = k8sKboxClient;
        // Informers
        this.kopologyInformer = kopologyInformer;
        this.kistributionInformer = kistributionInformer;
        this.kboxInformer = kboxInformer;
        // Init
        this.workqueue = new ArrayBlockingQueue<>(1024);
        this.kopologyLister = new Lister<>(kopologyInformer.getIndexer(), kubernetesClient.getNamespace());
        this.kistributionLister = new Lister<>(kistributionInformer.getIndexer(), kubernetesClient.getNamespace());
        this.kboxLister = new Lister<>(kboxInformer.getIndexer(), kubernetesClient.getNamespace());
    }

    public void initialize() {
        kopologyInformer.addEventHandler(new ResourceEventHandler<Kopology>() {

            @Override
            public void onUpdate(Kopology oldObj, Kopology newObj) {
                enqueueKopology(newObj);
                LOGGER.info("Kopology UPDATED: " + newObj.getMetadata().getName());

            }

            @Override
            public void onDelete(Kopology obj, boolean deletedFinalStateUnknown) {
                enqueueKopology(obj);
                LOGGER.info("Kopology mark for DELETION: " + obj.getMetadata().getName());
            }

            @Override
            public void onAdd(Kopology obj) {
                enqueueKopology(obj);
                LOGGER.info("Kopology ADDED: " + obj.getMetadata().getName());
            }

        });

        kistributionInformer.addEventHandler(new ResourceEventHandler<Kistribution>() {

            @Override
            public void onAdd(Kistribution obj) {
                enqueueKistribution(obj);
                LOGGER.info("Kistribution ADDED: " + obj.getMetadata().getName());
            }

            @Override
            public void onUpdate(Kistribution oldObj, Kistribution newObj) {
                enqueueKistribution(newObj);
                LOGGER.info("Kistribution UPDATED: " + newObj.getMetadata().getName());

            }

            @Override
            public void onDelete(Kistribution obj, boolean deletedFinalStateUnknown) {
                enqueueKistribution(obj);
                LOGGER.info("Kistribution mark for DELETION: " + obj.getMetadata().getName());

            }
        });

        kboxInformer.addEventHandler(new ResourceEventHandler<Kbox>() {

            @Override
            public void onAdd(Kbox obj) {
                enqueueKbox(obj);
                LOGGER.info("Kbox ADDED: " + obj.getMetadata().getName());

            }

            @Override
            public void onUpdate(Kbox oldObj, Kbox newObj) {
                enqueueKbox(newObj);
                LOGGER.info("Kistribution UPDATED: " + newObj.getMetadata().getName());

            }

            @Override
            public void onDelete(Kbox obj, boolean deletedFinalStateUnknown) {
                enqueueKbox(obj);
                LOGGER.info("Kbox mark for DELETION: " + obj.getMetadata().getName());
            }
        });
    }

    private void enqueueKopology(Kopology kopology) {
        LOGGER.info("enqueueKopology(" + kopology.getMetadata().getName() + ")");
        String key = Cache.metaNamespaceKeyFunc(kopology);
        LOGGER.info("Going to enqueue key " + key);
        if (key != null || !key.isEmpty()) {
            LOGGER.info("Adding kopology to workqueue");
            workqueue.add(key);
        }
    }

    private void enqueueKistribution(Kistribution kistribution) {
        LOGGER.info("enqueueKistribution(" + kistribution.getMetadata().getName() + ")");
        String key = Cache.metaNamespaceKeyFunc(kistribution);
        LOGGER.info("Going to enqueue key " + key);
        if (key != null || !key.isEmpty()) {
            LOGGER.info("Adding kistribution to workqueue");
            workqueue.add(key);
        }
    }

    private void enqueueKbox(Kbox kbox) {
        LOGGER.info("enqueueKbox(" + kbox.getMetadata().getName() + ")");
        String key = Cache.metaNamespaceKeyFunc(kbox);
        LOGGER.info("Going to enqueue key " + key);
        if (key != null || !key.isEmpty()) {
            LOGGER.info("Adding kbox to workqueue");
            workqueue.add(key);
        }
    }

    public void run() {
        LOGGER.info("Starting Kcdn controller");
        while (!kopologyInformer.hasSynced())

            while (true) {
                try {
                    LOGGER.info("Trying to fetch item from workqueue...");
                    if (workqueue.isEmpty()) {
                        LOGGER.info("Work Queue is empty");
                    }
                    String key = workqueue.take();
                    LOGGER.info("Got " + key);
                    if (key == null || key.isEmpty() || (!key.contains("/"))) {
                        LOGGER.warn("Invalid resource key: " + key);
                    }

                    // Get the resource's name from key which is in format namespace/name
                    String name = key.split("/")[1];
                    Kopology kopology = kopologyLister.get(key.split("/")[1]);
                    Kistribution kistribution = kistributionLister.get(key.split("/")[1]);
                    Kbox kbox = kboxLister.get(key.split("/")[1]);
                    if (kopology == null && kistribution == null && kbox != null) {
                        reconcile(kbox);
                    } else if (kopology == null && kistribution != null && kbox == null) {
                        reconcile(kistribution);
                    } else if (kopology != null && kbox == null && kistribution == null) {
                        reconcile(kopology);
                    } else {
                        LOGGER.error(name + " in workqueue no longer exists");
                        return;
                    }

                } catch (InterruptedException interruptedException) {
                    LOGGER.error("Controller interrupted..");
                }
            }
    }

    private void reconcile(CustomResource customResource) {
        if (customResource instanceof Kbox) {
            // reconcile Kbox
            Kbox kbox = (Kbox) customResource;
            String kboxDistribution = kbox.getSpec().getDistribution();
            String kboxDistributionStrategy = kbox.getSpec().getDistributionStrategy();
            LOGGER.info("Reconcile kbox with distribution: " + kboxDistribution);
            getKistributionByName(kboxDistribution).ifPresent(kistribution -> {
                kistribution.getSpec().getDistributionTypes().stream()
                        .filter(distributionType -> distributionType.equals(kboxDistributionStrategy)).findFirst()
                        .ifPresent(distributionType -> {
                            List<String> zones = kistribution.getSpec().getDistributionDefinitions()
                                    .get(distributionType);
                            for (String zone : zones) {
                                Deployment deployment = buildKboxDeployment(kbox, zone);
                                kubernetesClient.apps().deployments().inNamespace("default").createOrReplace(deployment);
                            }

                        });
            });

        }

    }

    private Deployment buildKboxDeployment(Kbox kbox, String zone) {
        Map<String, String> labels = new Hashtable<>();
        labels.put("app", "vbox-" + zone);
        Map<String, String> nodeSelectorLabels = new Hashtable<>();
        nodeSelectorLabels.put("region.id", zone);

        return new DeploymentBuilder().withNewMetadata().withName("vbox-deploy-" + zone)
                .withNamespace(kbox.getMetadata().getNamespace()).addNewOwnerReference().withController(true)
                .withKind("Kbox").withApiVersion("kcdn.rnp.br/v1alpha1").withName(kbox.getMetadata().getName())
                .withNewUid(kbox.getMetadata().getUid()).endOwnerReference().endMetadata().withNewSpec()
                .withReplicas(kbox.getSpec().getReplicas()).withNewSelector().withMatchLabels(labels).endSelector()
                .withNewTemplate().withNewMetadata().withLabels(labels).endMetadata().withNewSpec().addNewContainer()
                .withName("vbox").withImage("nginx:stable").addNewPort().withContainerPort(80).endPort()
                .withImagePullPolicy("IfNotPresent").endContainer().withNodeSelector(nodeSelectorLabels).endSpec()
                .endTemplate().endSpec().build();

        /*
         * return new DeploymentBuilder().withNewMetadata().withName("vbox-deploy")
         * .withNamespace(kbox.getMetadata().getNamespace()).addNewOwnerReference().
         * withController(true)
         * .withKind("Kbox").withApiVersion("kcdn.rnp.br/v1alpha1").withName(kbox.
         * getMetadata().getName())
         * .withNewUid(kbox.getMetadata().getUid()).endOwnerReference().endMetadata().
         * withNewSpec()
         * .withReplicas(kbox.getSpec().getReplicas()).withNewSelector().withMatchLabels
         * (labels).endSelector()
         * .withNewTemplate().withNewMetadata().withLabels(labels).endMetadata().
         * withNewSpec().withNewAffinity()
         * .withNewPodAffinity().addNewRequiredDuringSchedulingIgnoredDuringExecution().
         * withNewLabelSelector()
         * .addNewMatchExpression().withKey("region.id").withNewOperator("In").
         * withValues(zone) .endMatchExpression().endLabelSelector()
         * .withNewTopologyKey("failure-domain.beta.kubernetes.io/" + zone)
         * .endRequiredDuringSchedulingIgnoredDuringExecution().endPodAffinity().
         * endAffinity().addNewContainer()
         * .withName("vbox").withImage("nginx:stable").addNewPort().withContainerPort(80
         * ).endPort().endContainer() .endSpec().endTemplate().endSpec().build();
         */

    }

    private Optional<Kistribution> getKistributionByName(String kboxDistribution) {

        Optional<Kistribution> optionalKistribution = k8sKistributionClient.list().getItems().stream()
                .filter(k -> k.getMetadata().getName().equals(kboxDistribution)).findFirst();
        return optionalKistribution;
    }

    private Pod createNewPod(Kbox kbox, List<String> zones) {
        SpecNested<PodBuilder> spec = new PodBuilder().withNewMetadata()
                .withGenerateName(kbox.getMetadata().getName() + "-pod")
                .withNamespace(kbox.getMetadata().getNamespace())
                .withLabels(Collections.singletonMap("vbox", kbox.getMetadata().getName())).addNewOwnerReference()
                .withController(true).withKind("Kbox").withApiVersion("kcdn.rnp.br/v1alpha1")
                .withName(kbox.getMetadata().getName()).withNewUid(kbox.getMetadata().getUid()).endOwnerReference()
                .endMetadata().withNewSpec().addNewContainer().withName("busybox").withImage("busybox")
                .withCommand("sleep", "3600").endContainer();

        return null;

    }

    private OwnerReference getControllerOf(Pod pod) {
        List<OwnerReference> ownerReferences = pod.getMetadata().getOwnerReferences();
        for (OwnerReference ownerReference : ownerReferences) {
            if (ownerReference.getController().equals(Boolean.TRUE)) {
                return ownerReference;
            }
        }
        return null;
    }

}