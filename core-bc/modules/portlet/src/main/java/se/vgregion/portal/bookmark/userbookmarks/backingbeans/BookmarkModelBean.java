package se.vgregion.portal.bookmark.userbookmarks.backingbeans;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * @author Monica Olsson 2014-03-06
 *
 */
@Component(value = "bookmarkModelBean")
@Scope("session")
public class BookmarkModelBean implements Serializable {
    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkModelBean.class);

    private long id;
    private long companyId;
    private long groupId;
    private long userId;

    @Size(max = 255, message = "Titeln får inte vara längre än {max} tecken.")
    private String title;

    @Size(max= 500, message = "URL:en får inte vara längre än {max} tecken.")
    @NotBlank(message = "URL-fältet måste vara ifyllt.")
    private String url;

    @Size(max= 1000, message = "Beskrivningen får inte vara längre än {max} tecken.")
    protected String description;

    /**
     * Empty constructor
     */
    public BookmarkModelBean() {}

    public BookmarkModelBean(long id, String title, String url, String description) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.description = description;
    }

    /**
     * @return the companyId
     */
    public long getCompanyId() {
        return companyId;
    }

    /**
     * @param companyId
     *            the companyId to set
     */
    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }

    /**
     * @return the groupId
     */
    public long getGroupId() {
        return groupId;
    }

    /**
     * @param groupId
     *            the groupId to set
     */
    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the userId
     */
    public long getUserId() {
        return userId;
    }

    /**
     * @param userId
     *            the userId to set
     */
    public void setUserId(long userId) {
        this.userId = userId;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     *            the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
