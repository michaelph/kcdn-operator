package br.rnp.kcdnoperator.crs.kopology;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class Kopology extends CustomResource {

    @JsonProperty
    private KopologySpec spec;

    @Override
    public ObjectMeta getMetadata() {
        return super.getMetadata();
    }

    /**
     * @return the spec
     */
    public KopologySpec getSpec() {
        return spec;
    }

    /**
     * @param spec the spec to set
     */
    public void setSpec(KopologySpec spec) {
        this.spec = spec;
    }
}