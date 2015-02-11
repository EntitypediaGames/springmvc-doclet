package org.entitypedia.games.sdoclet.swagger.model;

/**
 * @author <a href="http//autayeu.com/">Aliaksandr Autayeu</a>
 */
public class ApiInfo {

    /**
     * First sentence of the package comment.
     */
    private String title;

    /**
     * The package comment minus first sentence.
     */
    private String description;

    private String termsOfServiceUrl;
    private String contact;
    private String license;
    private String licenseUrl;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTermsOfServiceUrl() {
        return termsOfServiceUrl;
    }

    public void setTermsOfServiceUrl(String termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getLicenseUrl() {
        return licenseUrl;
    }

    public void setLicenseUrl(String licenseUrl) {
        this.licenseUrl = licenseUrl;
    }
}
