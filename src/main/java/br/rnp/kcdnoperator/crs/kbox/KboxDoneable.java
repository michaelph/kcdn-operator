package br.rnp.kcdnoperator.crs.kbox;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KboxDoneable extends CustomResourceDoneable<Kbox> {

    public KboxDoneable(Kbox resource, Function<Kbox, Kbox> function) {
        super(resource, function);
    }

}