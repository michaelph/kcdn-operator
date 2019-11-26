package br.rnp.kcdnoperator.controllers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.rnp.kcdnoperator.crs.kopology.Kopology;
import br.rnp.kcdnoperator.crs.kopology.KopologyDoneable;
import br.rnp.kcdnoperator.crs.kopology.KopologyList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.informers.ResourceEventHandler;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.cache.Cache;
import io.fabric8.kubernetes.client.informers.cache.Lister;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class KopologyController {
    private static final Logger LOGGER = LoggerFactory.getLogger(KopologyController.class);
    private KubernetesClient kubernetesClient;
    private MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> k8sKopologyClient;
    private SharedIndexInformer<Kopology> kopologyInformer;
    private Lister<Kopology> kopologyLister;
    private BlockingQueue<String> workqueue;

    public KopologyController(KubernetesClient kubernetesClient,
            MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> k8sKopologyClient,
            SharedIndexInformer<Kopology> kopologyInformer) {
        this.kubernetesClient = kubernetesClient;
        this.k8sKopologyClient = k8sKopologyClient;
        this.kopologyInformer = kopologyInformer;
        this.kopologyLister = new Lister<>(kopologyInformer.getIndexer(), kubernetesClient.getNamespace());
        this.workqueue = new ArrayBlockingQueue<>(1024);
    }

    public void initialize() {
        kopologyInformer.addEventHandler(new ResourceEventHandler<Kopology>() {

            @Override
            public void onUpdate(Kopology oldObj, Kopology newObj) {
                enqueueKopology(newObj);
                LOGGER.info("Kopology UPDATED: from ");

            }

            @Override
            public void onDelete(Kopology obj, boolean deletedFinalStateUnknown) {
                enqueueKopology(obj);
                LOGGER.info("Kopology mark for DELETION");
            }

            @Override
            public void onAdd(Kopology obj) {
                enqueueKopology(obj);
                LOGGER.info("Kopology ADDED:");
            }
        });
    }

    private void enqueueKopology(Kopology kopology) {
        LOGGER.info("enqueueKopology(" + kopology.getMetadata().getName() + ")");
        String key = Cache.metaNamespaceKeyFunc(kopology);
        LOGGER.info("Going to enqueue key " + key);
        if (key != null || !key.isEmpty()) {
            LOGGER.info("Adding item to workqueue");
            workqueue.add(key);
        }
    }

    public void run() {
        LOGGER.info("Starting Kopology controller");
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

                    // Get the Kopology resource's name from key which is in format namespace/name
                    String name = key.split("/")[1];
                    Kopology kopology = kopologyLister.get(key.split("/")[1]);
                    if (kopology == null) {
                        LOGGER.error("Kopology " + name + " in workqueue no longer exists");
                        return;
                    }
                    // reconcile(kopology);

                } catch (InterruptedException interruptedException) {
                    LOGGER.error("Controller interrupted..");
                }
            }
    }

    /**
     * @return the k8sKopologyClient
     */
    public MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> getK8sKopologyClient() {
        return k8sKopologyClient;
    }
}