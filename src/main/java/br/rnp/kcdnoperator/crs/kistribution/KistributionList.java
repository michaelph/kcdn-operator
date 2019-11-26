package br.rnp.kcdnoperator.crs.kistribution;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.client.CustomResourceList;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KistributionList extends CustomResourceList<Kistribution>{

}