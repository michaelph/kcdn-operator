package br.rnp.kcdnoperator;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import br.rnp.kcdnoperator.controllers.KcdnController;
import br.rnp.kcdnoperator.crs.kbox.Kbox;
import br.rnp.kcdnoperator.crs.kbox.KboxDoneable;
import br.rnp.kcdnoperator.crs.kbox.KboxList;
import br.rnp.kcdnoperator.crs.kistribution.Kistribution;
import br.rnp.kcdnoperator.crs.kistribution.KistributionDoneable;
import br.rnp.kcdnoperator.crs.kistribution.KistributionList;
import br.rnp.kcdnoperator.crs.kopology.Kopology;
import br.rnp.kcdnoperator.crs.kopology.KopologyDoneable;
import br.rnp.kcdnoperator.crs.kopology.KopologyList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.client.dsl.base.CustomResourceDefinitionContext;
import io.fabric8.kubernetes.client.informers.SharedIndexInformer;
import io.fabric8.kubernetes.client.informers.SharedInformerFactory;
import io.quarkus.runtime.StartupEvent;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Operator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Operator.class);
    @Inject
    KubernetesClient kubernetesClient;
    @Inject
    MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> k8sKopologyClient;
    @Inject
    MixedOperation<Kistribution, KistributionList, KistributionDoneable, Resource<Kistribution, KistributionDoneable>> k8sKistributionClient;
    @Inject
    MixedOperation<Kbox, KboxList, KboxDoneable, Resource<Kbox, KboxDoneable>> k8sKboxClient;

    public void init(@Observes StartupEvent e) {

        // Contexts
        LOGGER.info("Building contexts!");
        CustomResourceDefinitionContext kopologyCustomResourceDefinitionContext = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha1").withScope("Namespaced").withGroup("kcdn.rnp.br").withPlural("kopologies")
                .build();
        CustomResourceDefinitionContext kistributionCustomResourceDefinitionContext = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha1").withScope("Namespaced").withGroup("kcdn.rnp.br").withPlural("kistributions")
                .build();
        CustomResourceDefinitionContext kboxCustomResourceDefinitionContext = new CustomResourceDefinitionContext.Builder()
                .withVersion("v1alpha1").withScope("Namespaced").withGroup("kcdn.rnp.br").withPlural("kboxes").build();

        // Informers
        LOGGER.info("Building informers!");
        SharedInformerFactory informerFactory = kubernetesClient.informers();
        SharedIndexInformer<Kopology> kopologyInformer = informerFactory.sharedIndexInformerForCustomResource(
                kopologyCustomResourceDefinitionContext, Kopology.class, KopologyList.class, 10 * 60 * 1000);
        SharedIndexInformer<Kistribution> kistributionInformer = informerFactory.sharedIndexInformerForCustomResource(
                kistributionCustomResourceDefinitionContext, Kistribution.class, KistributionList.class,
                10 * 60 * 1000);
        SharedIndexInformer<Kbox> kboxInformer = informerFactory.sharedIndexInformerForCustomResource(
                kboxCustomResourceDefinitionContext, Kbox.class, KboxList.class, 10 * 60 * 1000);

        // Controllers
        LOGGER.info("Building controllers!");
        KcdnController kcdnController = new KcdnController(kubernetesClient, k8sKopologyClient, k8sKistributionClient,
                k8sKboxClient, kopologyInformer, kistributionInformer, kboxInformer);

        LOGGER.info("Initializing  controllers!");
        kcdnController.initialize();
        informerFactory.startAllRegisteredInformers();
        kcdnController.run();

    }

}
