package br.rnp.kcdnoperator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

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
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class CResourcesClientsProducer {
    private static final String NAMESPACE = "default";
    private static final Logger LOGGER = LoggerFactory.getLogger(CResourcesClientsProducer.class);

    @Produces
    @Singleton
    MixedOperation<Kopology, KopologyList, KopologyDoneable, Resource<Kopology, KopologyDoneable>> getKopologyClient(
            final KubernetesClient defaultKubernetesClient) throws Exception {
        KubernetesDeserializer.registerCustomKind("kcdn.rnp.br/v1alpha1", "Kopology", Kopology.class);
        final CustomResourceDefinition customResourceDefinition = defaultKubernetesClient.customResourceDefinitions()
                .list().getItems().stream().filter(crd -> crd.getMetadata().getName().equals("kopologies.kcdn.rnp.br"))
                .findAny().orElseThrow(Exception::new);
        return defaultKubernetesClient.customResources(customResourceDefinition, Kopology.class, KopologyList.class,
                KopologyDoneable.class);
    }

    @Produces
    @Singleton
    MixedOperation<Kistribution, KistributionList, KistributionDoneable, Resource<Kistribution, KistributionDoneable>> getKistributionClient(
            final KubernetesClient defaultKubernetesClient) throws Exception {
        KubernetesDeserializer.registerCustomKind("kcdn.rnp.br/v1alpha1", "Kistribution", Kistribution.class);
        final CustomResourceDefinition customResourceDefinition = defaultKubernetesClient.customResourceDefinitions()
                .list().getItems().stream()
                .filter(crd -> crd.getMetadata().getName().equals("kistributions.kcdn.rnp.br")).findAny()
                .orElseThrow(Exception::new);
        return defaultKubernetesClient.customResources(customResourceDefinition, Kistribution.class,
                KistributionList.class, KistributionDoneable.class);
    }

    @Produces
    @Singleton
    MixedOperation<Kbox, KboxList, KboxDoneable, Resource<Kbox, KboxDoneable>> getKboxClient(
            final KubernetesClient defaultKubernetesClient) throws Exception {
        KubernetesDeserializer.registerCustomKind("kcdn.rnp.br/v1alpha1", "Kbox", Kbox.class);
        final CustomResourceDefinition customResourceDefinition = defaultKubernetesClient.customResourceDefinitions()
                .list().getItems().stream().filter(crd -> crd.getMetadata().getName().equals("kboxes.kcdn.rnp.br"))
                .findAny().orElseThrow(Exception::new);
        return defaultKubernetesClient.customResources(customResourceDefinition, Kbox.class, KboxList.class,
                KboxDoneable.class);
    }

    @Produces
    @Singleton
    KubernetesClient getKubernetesClient(@Named("operatorConfig") final Optional<String> optionalConfig) {
        if (optionalConfig.isPresent()) {

            try {
                final FileInputStream is = new FileInputStream(optionalConfig.get());
                return DefaultKubernetesClient.fromConfig(is).inNamespace(NAMESPACE);
            } catch (final KubernetesClientException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (final FileNotFoundException e) {
                LOGGER.error(e.getMessage(), e);
            }
        } else {
            LOGGER.warn("Operator config not found!");
            LOGGER.info("Assuming kubernetes environment!");
            try (KubernetesClient client = new DefaultKubernetesClient()) {
                return client;
            } catch (KubernetesClientException e) {
                LOGGER.error(e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }

        }
        return null;
    }

    @Produces
    @Singleton
    @Named("operatorConfig")
    private Optional<String> loadOperatorConfig() {
        String kubeConfigPath = null;

        final Map<String, String> map = System.getenv();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            if (entry.getKey().equals("KUBECONFIG")) {
                System.out.println("KUBECONFIG:- " + entry.getKey() + " Value:- " + entry.getValue());
                kubeConfigPath = entry.getValue();
            }

        }
        return Optional.ofNullable(kubeConfigPath);

    }
}
