package br.rnp.kcdnoperator.crs.kopology;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class KopologyDoneable extends CustomResourceDoneable<Kopology> {

    public KopologyDoneable(Kopology resource, Function<Kopology, Kopology> function) {
        super(resource, function);
    }

}