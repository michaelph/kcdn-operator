package br.rnp.kcdnoperator.crs.kopology;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.quarkus.runtime.annotations.RegisterForReflection;

@JsonDeserialize
@RegisterForReflection
public class KopologySpec {

    @JsonProperty
    private Set<String> regions;

    @JsonProperty
    private Map<String, List<String>> links;

    /**
     * @return the regions
     */
    public Set<String> getRegions() {
        return regions;
    }

    /**
     * @param regions the regions to set
     */
    public void setRegions(Set<String> regions) {
        this.regions = regions;
    }

    /**
     * @return the links
     */
    public Map<String, List<String>> getLinks() {
        return links;
    }

    /**
     * @param links the links to set
     */
    public void setLinks(Map<String, List<String>> links) {
        this.links = links;
    }

}