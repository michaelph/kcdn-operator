package br.rnp.kcdnoperator.controllers;

import javax.inject.Inject;

import br.rnp.kcdnoperator.crs.kistribution.Kistribution;
import br.rnp.kcdnoperator.crs.kistribution.KistributionDoneable;
import br.rnp.kcdnoperator.crs.kistribution.KistributionList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

public class KistributionController {

    @Inject
    KubernetesClient kubernetesClient;
    @Inject
    MixedOperation<Kistribution, KistributionList, KistributionDoneable, Resource<Kistribution, KistributionDoneable>> k8sKistributionClient;
}