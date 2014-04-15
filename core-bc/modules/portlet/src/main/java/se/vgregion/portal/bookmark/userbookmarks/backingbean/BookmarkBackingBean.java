package se.vgregion.portal.bookmark.userbookmarks.backingbean;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import se.vgregion.portal.bookmark.domain.jpa.Bookmark;
import se.vgregion.portal.bookmark.exception.CreateBookmarkException;
import se.vgregion.portal.bookmark.exception.UpdateBookmarkException;
import se.vgregion.portal.bookmark.service.BookmarkService;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.portlet.PortletRequest;
import java.io.Serializable;
import java.util.List;

/**
 * @author Monica Olsson 2014-03-06
 */
@Component
@Scope("session")
public class BookmarkBackingBean implements Serializable{
    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkBackingBean.class);
    private List<Bookmark> vgrBookmarks;
    private List<Bookmark> customBookmarks;

    protected long bookmarkId;
    private long companyId;
    private long userId;
    private long scopeGroupId;

    //For navigation purposes.
    private boolean editBookmarkState = false;
    //To know if the user saves or edits a bookmark.
    private boolean editBookmark;

    //Primefaces paginator configuration
    private String paginatorFirstPageLink;
    private String PaginatorLastPageLink;

    private int paginatorRows = 5;
    private int paginatorPageLinks = 5;

    @Autowired
    private BookmarkService bookmarkService;

    @Value("#{bookmarkModelBean}")
    private BookmarkModelBean bookmarkModelBean;

    /**
     * Empty constructor
     */
    public BookmarkBackingBean() { }

    /**
     * Gets ThemeDisplay and sets the companyId, userId and scopeGroupId.
     */
    public void getThemeDisplayIds(){
        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        PortletRequest portletRequest = (PortletRequest) externalContext.getRequest();
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);

        companyId = themeDisplay.getCompanyId();
        userId = themeDisplay.getUserId();
        scopeGroupId = themeDisplay.getScopeGroupId();

    }

    /**
     * Get vgr Bookmarks method - provides user bookmarks to the view
     *
     * @return List of VGR Bookmarks
     */
    public List<Bookmark> getVgrBookmarks() {
        getThemeDisplayIds();
        vgrBookmarks = bookmarkService.findVgrBookmarksForUser(companyId, userId);

        return vgrBookmarks;
    }

    /**
     * Get custom Bookmarks method - provides user bookmarks to the view
     *
     * @return List of custom Bookmarks
     */
    public List<Bookmark> getCustomBookmarks() {
        getThemeDisplayIds();
        customBookmarks = bookmarkService.findUserBookmarks(companyId, scopeGroupId, userId);

        return customBookmarks;
    }

    /**
     * If the bookmark portlet is in edit mode this method will save the bookmark and exit the method.
     * If not it checks if the url already exists. If it does a dialog will give the user the option to
     * overwrite the old bookmark. If the url does not exist already the bookmark is saved with no warning.
     */
    public void checkIfUrlExists(){
        //Stop checking url if bookmark portlet is in edit mode.
        if(editBookmark){
            saveBookmark();
            return;
        }

        String url = bookmarkModelBean.getUrl();

        for(Bookmark bookmark : customBookmarks){

            if(url.equals(bookmark.getUrl().replace("http://", "")) || url.equals(bookmark.getUrl())){
                facesErrorMessage("Ett bokmärke med URL:en " + url + " finns redan.");

                return;
            }
        }

        //Checks if the user tries to add more bookmarks than allowed.
        if(getCustomBookmarks().size() >= 1000) {
            facesErrorMessage("Du får max ha 1000 bokmärken. " +
                    "Radera gamla bokmärken för att få plats med nya.");
            return;
        }
        saveBookmark();
    }

    /**
     * Primefaces error message shown in edit-bookmark.xhtml <p:messages/>
     * @param errorMessage
     */
    public void facesErrorMessage(String errorMessage) {
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, errorMessage,  ""));
    }

    /**
     * The show edit bookmark method - will provide view with a Bookmark from the database if
     * a valid bookmarkId exists.
     *
     * @return the view
     */
    public String saveBookmark() {
        getThemeDisplayIds();

        String url = bookmarkModelBean.getUrl();
        String title = bookmarkModelBean.getTitle();

        //Create a title from the URL if it's not filled in by the user
        if (title == null || title.equals("")) {

            if (url.contains("http://")) {
                title = url.replace("http://", "");

            } else if (url.contains("https://")) {
                title = url.replace("https://", "");
            }
            //If the URL is longer than 255 characters it's cut of at 255 to be a valid title.
            if(url.length() > 255) {
                title = url.substring(0, 255);
            } else {
                title = url;
            }
        }

        // Check http
        if (url != null && !url.startsWith("http")) {
            url = "http://" + url;
        }

        if (bookmarkModelBean.getId() == 0) {

            Bookmark newBookmark = new Bookmark(companyId, scopeGroupId, userId, title, url,
                    bookmarkModelBean.getDescription());
            try {
                bookmarkService.addBookmark(newBookmark);
            } catch (CreateBookmarkException e) {
                LOGGER.error(e.getMessage(), e);
            }

        } else {
            try {
                bookmarkService.updateBookmark(bookmarkModelBean.getId(), title,
                        url, bookmarkModelBean.getDescription());
            } catch (UpdateBookmarkException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        clearBookmarkModelBean();

        this.editBookmarkState = false;

        return null;
    }

    /**
     * Method clears the BookmarkModelBean from values so that the edit_bookmark form will be empty
     */
    public void clearBookmarkModelBean() {

        this.bookmarkModelBean.setTitle(null);
        this.bookmarkModelBean.setDescription(null);
        this.bookmarkModelBean.setUrl(null);
        this.bookmarkModelBean.setId(0);

    }

    /**
     * Method for deleting a Bookmark from the database
     *
     * @return the view
     */
    public void deleteBookmark() {

        if (bookmarkId != 0) {
            bookmarkService.deleteBookmark(bookmarkId);

        }
    }

    /**
     * Navigate to edit Bookmark view
     * Sets EditBookmarkState to true
     * Clears BookmarkModelBean
     */
    public void addNewBookmark() {
        //To know that it's a new bookmark.
        editBookmark = false;
        //To help navigate.
        editBookmarkState = true;

    }

    /**
     * If BookmarkId exists this method will retrieve a Bookmark from the database
     * the BookmarkModelBean is filled with parameters from the Bookmark that will be displayed in the form.
     * Sets the Primefaces TabIndex to 1 to navigate to the form.
     */
    public void editBookmark() {

        Bookmark bookmark = new Bookmark();

        if (bookmarkId != 0) {
            bookmark = bookmarkService.find(bookmarkId);

            bookmarkModelBean.setDescription(bookmark.getDescription());
            bookmarkModelBean.setTitle(bookmark.getTitle());
            bookmarkModelBean.setUrl(bookmark.getUrl().replace("http://", ""));
            bookmarkModelBean.setId(bookmark.getId());

        }
        //To know that it's an old bookmark.
        editBookmark = true;
        //To help navigate.
        editBookmarkState = true;
    }

    /**
     * Decides the visability of Primefaces navigation button FirstPageLink.
     * The button is hidden if customBookmarks list doesn't exceed paginatorMaxPageLinks * paginatorMaxRows
     *
     * @return "{FirstPageLink}" or "" depending on customBookmarks list size
     */
    public String getPaginatorFirstPageLink() {
        if(getCustomBookmarks().size() <= paginatorPageLinks * paginatorRows){
            return "";
        }
        return "{FirstPageLink}";
    }

    /**
     * Decides the visability of Primefaces navigation button LastPageLink.
     * The button is hidden if customBookmarks list doesn't exceed paginatorMaxPageLinks * paginatorMaxRows
     *
     * @return "{LastPageLink}" or "" depending on customBookmarks list size.
     */
    public String getPaginatorLastPageLink() {
        if(getCustomBookmarks().size() <= paginatorPageLinks * paginatorRows){
            return "";
        }
        return "{LastPageLink}";
    }

    /**
     * Clears the BookmarkModelBean and sets editBookmarkState to false.
     */
    public void editBookmarkStateFalse() {
        clearBookmarkModelBean();
        this.editBookmarkState = false;
    }

    /**
     * @param bookmarkModelBean the bookmarkBean to set
     */
    public void setBookmarkModelBean(BookmarkModelBean bookmarkModelBean) {
        this.bookmarkModelBean = bookmarkModelBean;
    }

    public BookmarkModelBean getBookmarkModelBean() {
        return bookmarkModelBean;
    }

    public long getBookmarkId() {
        return bookmarkId;
    }

    public void setBookmarkId(long bookmarkId) {
        this.bookmarkId = bookmarkId;
    }

    public BookmarkService getBookmarkService() {
        return bookmarkService;
    }

    public void setBookmarkService(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    public boolean isEditBookmarkState() {
        return editBookmarkState;
    }

    public void setEditBookmarkState(boolean editBookmarkState) {
        this.editBookmarkState = editBookmarkState;
    }

    public void editBookmarkStateTrue() {
        this.editBookmarkState = true;
    }

    public int getPaginatorRows() {
        return paginatorRows;
    }

    public void setPaginatorRows(int paginatorRows) {
        this.paginatorRows = paginatorRows;
    }

    public int getPaginatorPageLinks() {
        return paginatorPageLinks;
    }

    public void setPaginatorPageLinks(int paginatorPageLinks) {
        this.paginatorPageLinks = paginatorPageLinks;
    }

    public boolean isEditBookmark() {
        return editBookmark;
    }

    public void setEditBookmark(boolean editBookmark) {
        this.editBookmark = editBookmark;
    }

    public void setCustomBookmarks(List<Bookmark> customBookmarks) {
        this.customBookmarks = customBookmarks;
    }

    public void setVgrBookmarks(List<Bookmark> vgrBookmarks) {
        this.vgrBookmarks = vgrBookmarks;
    }
}