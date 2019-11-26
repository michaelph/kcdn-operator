package br.rnp.kcdnoperator.crs.kistribution;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KistributionDoneable extends CustomResourceDoneable<Kistribution> {

    public KistributionDoneable(Kistribution resource, Function<Kistribution, Kistribution> function) {
        super(resource, function);
    }

}