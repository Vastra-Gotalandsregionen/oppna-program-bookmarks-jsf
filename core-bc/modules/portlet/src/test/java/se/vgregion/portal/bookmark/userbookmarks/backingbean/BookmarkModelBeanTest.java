package se.vgregion.portal.bookmark.userbookmarks.backingbean;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Monica on 2014-01-27.
 */
public class BookmarkModelBeanTest {
    BookmarkModelBean bookmarkModelBean;
    @Before
    public void setUp() throws Exception {
        bookmarkModelBean = new BookmarkModelBean();
    }

    @Test
    public void testBookmarkBeanConstructor() throws Exception {
        //When
        bookmarkModelBean = new BookmarkModelBean(Long.valueOf(1), "VGR", "www.vgr.se", "My vgr bookmark");

        //Then
        assertEquals(1, bookmarkModelBean.getId());
        assertEquals("VGR", bookmarkModelBean.getTitle());
        assertEquals("www.vgr.se", bookmarkModelBean.getUrl());
        assertEquals("My vgr bookmark", bookmarkModelBean.getDescription());
    }

    @Test
    public void testCompanyId() throws Exception {
        //When
        bookmarkModelBean.setCompanyId(1);

        //Then
        assertEquals(1, bookmarkModelBean.getCompanyId());
    }

    @Test
    public void testGetGroupId() throws Exception {
        //When
        bookmarkModelBean.setGroupId(1);

        //Then
        assertEquals(1, bookmarkModelBean.getGroupId());
    }


    @Test
    public void testGetUserId() throws Exception {
        //When
        bookmarkModelBean.setUserId(1);

        //Then
        assertEquals(1, bookmarkModelBean.getUserId());

    }

}
