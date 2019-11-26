package br.rnp.kcdnoperator.crs.kistribution;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.client.CustomResource;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class Kistribution extends CustomResource {

    @JsonProperty
    private KistributionSpec spec;

    @Override
    public ObjectMeta getMetadata() {
        return super.getMetadata();
    }

    /**
     * @return the spec
     */
    public KistributionSpec getSpec() {
        return spec;
    }

    /**
     * @param spec the spec to set
     */
    public void setSpec(KistributionSpec spec) {
        this.spec = spec;
    }

}