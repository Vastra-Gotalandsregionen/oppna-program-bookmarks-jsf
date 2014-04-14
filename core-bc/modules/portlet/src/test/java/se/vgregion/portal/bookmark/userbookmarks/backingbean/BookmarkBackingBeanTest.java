package se.vgregion.portal.bookmark.userbookmarks.backingbean;

import com.liferay.portal.theme.ThemeDisplay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import se.vgregion.portal.bookmark.domain.jpa.Bookmark;
import se.vgregion.portal.bookmark.service.BookmarkService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Monica 2014-03-06
 */
@RunWith(MockitoJUnitRunner.class)
public class BookmarkBackingBeanTest {

    @Mock
    private BookmarkService bookmarkService;

    @Mock
    private BookmarkModelBean bookmarkModelBean;

    @Mock
    private ThemeDisplay themeDisplay;

    @InjectMocks
    private BookmarkBackingBean bookmarkBackingBean = new BookmarkBackingBean();

    private boolean facesErrorMessageHaveRun = false;
    private boolean saveBookmarkHaveRun = false;
    private boolean getThemeDisplayIdsHaveRun = false;


    @Test
    public void testCheckIfUrlExistsInEditBookmarkMode() throws Exception {
        //Given
        BookmarkBackingBean bookmarkBackingBean = new BookmarkBackingBean() {
            @Override
            public String saveBookmark() {
                saveBookmarkHaveRun = true;
                return "dummy value";
            }
        };

        bookmarkBackingBean.setEditBookmark(true);

        //When
        bookmarkBackingBean.checkIfUrlExists();

        //Then
        assertTrue(saveBookmarkHaveRun);
    }

    @Test
    public void testCheckIfUrlExists() throws Exception {
        //Given
        BookmarkBackingBean bookmarkBackingBean = new BookmarkBackingBean() {
            @Override
            public String saveBookmark() {
                saveBookmarkHaveRun = true;
                return "dummy value";
            }
        };

        List<Bookmark>customBookmarks1 = new ArrayList<Bookmark>();
        Bookmark bookmark= new Bookmark();
        bookmark.setTitle("Title");
        bookmark.setUrl("URL");
        customBookmarks1.add(bookmark);

        bookmarkBackingBean.setEditBookmark(true);
        bookmarkBackingBean.setCustomBookmarks(customBookmarks1);

        //When
        bookmarkBackingBean.checkIfUrlExists();

        //Then
        assertTrue(saveBookmarkHaveRun);

    }

    @Test
    public void testCheckIfUrlExistsDuplicateUrl() throws Exception {
        //Given
        BookmarkBackingBean bookmarkBackingBean = new BookmarkBackingBean() {
            @Override
            public void facesErrorMessage(String errorMessage) {
                facesErrorMessageHaveRun = true;
            }
        };

        List<Bookmark>customBookmarks1 = new ArrayList<Bookmark>();

        Bookmark bookmark= new Bookmark();
        bookmark.setTitle("Title");
        bookmark.setUrl("URL");
        customBookmarks1.add(bookmark);

        BookmarkModelBean bookmarkModelBean1 = new BookmarkModelBean();
        bookmarkModelBean1.setUrl("URL");

        bookmarkBackingBean.setBookmarkModelBean(bookmarkModelBean1);
        bookmarkBackingBean.setEditBookmark(false);
        bookmarkBackingBean.setCustomBookmarks(customBookmarks1);

        //When
        bookmarkBackingBean.checkIfUrlExists();

        //Then
        assertTrue(facesErrorMessageHaveRun);

    }

    @Test
    public void testSetBookmarkBean() throws Exception {
        //Given
        BookmarkBackingBean controller = new BookmarkBackingBean();
        BookmarkModelBean bookmark = new BookmarkModelBean();
        bookmark.setTitle("VGR");

        //When
        controller.setBookmarkModelBean(bookmark);

        //Then
        assertEquals("VGR", controller.getBookmarkModelBean().getTitle());
    }

    @Test
    public void testSaveBookmark() throws Exception {
        //Given
        BookmarkModelBean bookmarkModelBean1 = new BookmarkModelBean();
        bookmarkModelBean1.setTitle("VGR");
        bookmarkModelBean1.setUrl("http://");
        bookmarkModelBean1.setId(0);
        bookmarkModelBean1.setDescription("My url");

        BookmarkBackingBean bookmarkBackingBean = new BookmarkBackingBean() {
            @Override
            public void getThemeDisplayIds(){
                getThemeDisplayIdsHaveRun = true;
            }

        };

        bookmarkBackingBean.setBookmarkModelBean(bookmarkModelBean1);
        BookmarkService bookmarkService1 = mock(BookmarkService.class);
        bookmarkBackingBean.setBookmarkService(bookmarkService1);

        //When
        bookmarkBackingBean.saveBookmark();

        //Then
        verify(bookmarkService1).addBookmark(any(Bookmark.class));
        assertTrue(getThemeDisplayIdsHaveRun);

    }


    @Test
    public void testSaveBookmarkExistingId() throws Exception {
        //Given
        BookmarkModelBean bookmarkModelBean1 = new BookmarkModelBean();
        bookmarkModelBean1.setTitle("VGR");
        bookmarkModelBean1.setUrl("http://");
        bookmarkModelBean1.setId(1);
        bookmarkModelBean1.setDescription("My url");

        BookmarkBackingBean bookmarkBackingBean = new BookmarkBackingBean() {
            @Override
            public void getThemeDisplayIds(){
                getThemeDisplayIdsHaveRun = true;
            }

        };

        bookmarkBackingBean.setBookmarkModelBean(bookmarkModelBean1);
        BookmarkService bookmarkService1 = mock(BookmarkService.class);
        bookmarkBackingBean.setBookmarkService(bookmarkService1);

        //When
        bookmarkBackingBean.saveBookmark();

        //Then
        verify(bookmarkService1).updateBookmark(anyLong(),anyString(),anyString(),anyString());
        assertTrue(getThemeDisplayIdsHaveRun);

    }


    @Test
    public void testClearBookmarkBean() throws Exception {
        //When
        bookmarkBackingBean.clearBookmarkModelBean();

        //Then
        verify(bookmarkModelBean, times(1)).setTitle(null);
        verify(bookmarkModelBean, times(1)).setDescription(null);
        verify(bookmarkModelBean, times(1)).setUrl(null);
        verify(bookmarkModelBean, times(1)).setId(Long.valueOf(0));

    }


    @Test
    public void testEditBookmarkWithParameter() throws Exception {
        // Given
        bookmarkBackingBean.setBookmarkId(10);
        Bookmark bookmark = new Bookmark(1, 1, 1, "Title", "www.url.se", "My url description.");
        bookmark.setId(Long.valueOf(1));

        // When
        when(bookmarkService.find(10)).thenReturn(bookmark);
        bookmarkBackingBean.editBookmark();

        // Then
        verify(bookmarkService, times(1)).find(anyLong());

        verify(bookmarkModelBean, atLeast(1)).setDescription(anyString());
        verify(bookmarkModelBean, atLeast(1)).setTitle(anyString());
        verify(bookmarkModelBean, atLeast(1)).setUrl(anyString());
        verify(bookmarkModelBean, atLeast(1)).setId(anyLong());


    }

    @Test
    public void testEditBookmarkNoParameter() throws Exception {
        // When
        bookmarkBackingBean.editBookmark();

        // Then
        assertEquals(null, bookmarkModelBean.getDescription());
        assertEquals(null, bookmarkModelBean.getTitle());
        assertEquals(null, bookmarkModelBean.getUrl());
        assertEquals(0, bookmarkModelBean.getId());

    }

    @Test
    public void testDeleteBookmarkNoParameter() throws Exception {
        // When
        bookmarkBackingBean.setBookmarkId(1);
        bookmarkBackingBean.deleteBookmark();

        // Then
        verify(bookmarkService, times(1)).deleteBookmark(anyLong());

    }

    @Test
    public void deleteBookmarkWithParameter() throws Exception {
        // Given
        bookmarkBackingBean.setBookmarkId(10);

        // When
        bookmarkBackingBean.deleteBookmark();

        // Then
        verify(bookmarkService, times(1)).deleteBookmark(anyLong());

    }

    @Test
    public void testGetBookmarkId() throws Exception {
        //Given
        BookmarkBackingBean controller = new BookmarkBackingBean();

        //When
        controller.setBookmarkId(10);

        //Then
        assertEquals(10, controller.getBookmarkId());

    }

}
