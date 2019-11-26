package br.rnp.kcdnoperator.crs.kbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class Kbox extends CustomResource {

    @JsonProperty
    private KboxSpec spec;

    @Override
    public ObjectMeta getMetadata() {
        return super.getMetadata();
    }

    /**
     * @return the spec
     */
    public KboxSpec getSpec() {
        return spec;
    }

    /**
     * @param spec the spec to set
     */
    public void setSpec(KboxSpec spec) {
        this.spec = spec;
    }

}