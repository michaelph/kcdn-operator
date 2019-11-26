package br.rnp.kcdnoperator.crs.kistribution;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KistributionSpec {

    @JsonProperty("distribution-types")
    private List<String> distributionTypes;

    @JsonProperty("distribution-definitions")
    private Map<String, List<String>> distributionDefinitions;

    @JsonProperty
    private String replicas;

    /**
     * @return the distributionTypes
     */
    public List<String> getDistributionTypes() {
        return distributionTypes;
    }

    /**
     * @param distributionTypes the distributionTypes to set
     */
    public void setDistributionTypes(List<String> distributionTypes) {
        this.distributionTypes = distributionTypes;
    }

    /**
     * @return the distributionDefinitions
     */
    public Map<String, List<String>> getDistributionDefinitions() {
        return distributionDefinitions;
    }

    /**
     * @param distributionDefinitions the distributionDefinitions to set
     */
    public void setDistributionDefinitions(Map<String, List<String>> distributionDefinitions) {
        this.distributionDefinitions = distributionDefinitions;
    }

    /**
     * @return the replicas
     */
    public String getReplicas() {
        return replicas;
    }

    /**
     * @param replicas the replicas to set
     */
    public void setReplicas(String replicas) {
        this.replicas = replicas;
    }

}
