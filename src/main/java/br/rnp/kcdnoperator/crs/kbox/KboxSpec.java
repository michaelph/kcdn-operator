package br.rnp.kcdnoperator.crs.kbox;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.fabric8.kubernetes.api.model.apps.DeploymentSpec;
import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KboxSpec extends DeploymentSpec {

    @JsonProperty
    private String distribution;
    @JsonProperty("distribution-strategy")
    private String distributionStrategy;

    /**
     * @return the distribution
     */
    public String getDistribution() {
        return distribution;
    }

    /**
     * @param distribution the distribution to set
     */
    public void setDistribution(String distribution) {
        this.distribution = distribution;
    }

    /**
     * @return the distributionStrategy
     */
    public String getDistributionStrategy() {
        return distributionStrategy;
    }

    /**
     * @param distributionStrategy the distributionStrategy to set
     */
    public void setDistributionStrategy(String distributionStrategy) {
        this.distributionStrategy = distributionStrategy;
    }

}
