package br.rnp.kcdnoperator.crs.kopology;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResourceList;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KopologyList extends CustomResourceList<Kopology> {

}