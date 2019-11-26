package br.rnp.kcdnoperator.controllers;

import javax.inject.Inject;

import br.rnp.kcdnoperator.crs.kbox.Kbox;
import br.rnp.kcdnoperator.crs.kbox.KboxDoneable;
import br.rnp.kcdnoperator.crs.kbox.KboxList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.Resource;

public class KboxController {
    @Inject
    KubernetesClient kubernetesClient;
    @Inject
    MixedOperation<Kbox, KboxList, KboxDoneable, Resource<Kbox, KboxDoneable>> k8sKboxClient;
}