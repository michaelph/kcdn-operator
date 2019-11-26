package br.rnp.kcdnoperator;

import javax.inject.Inject;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;

public class VboxDeploymentManager {

    @Inject
    KubernetesClient kubernetesClient;

    public void deploy(String strategy) {
        DeploymentBuilder builder = new DeploymentBuilder();
    }

    public enum Strategies {
        CORE, EDGE,
    }


}
