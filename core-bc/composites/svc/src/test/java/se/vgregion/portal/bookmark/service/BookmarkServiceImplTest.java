package se.vgregion.portal.bookmark.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import se.vgregion.portal.bookmark.domain.jpa.Bookmark;
import se.vgregion.portal.bookmark.exception.CreateBookmarkException;
import se.vgregion.portal.bookmark.exception.UpdateBookmarkException;
import se.vgregion.portal.bookmark.repository.BookmarkRepository;

public class BookmarkServiceImplTest {

	BookmarkServiceImpl bookmarkServiceImpl;
	Bookmark bookmark_new;
	Bookmark bookmark_1;

	long bookmarkId_1;
	long companyId;
	long groupId;
	String screenName;

	@Before
	public void setup() {

		BookmarkRepository bookmarkRepository = Mockito.mock(BookmarkRepository.class);

		bookmarkServiceImpl = new BookmarkServiceImpl(bookmarkRepository){
			@Override
			protected String[] getIntraUris(long companyId, String screenName) {
				return new String[0];
			}
		};

		bookmark_new = new Bookmark();

		bookmarkId_1 = 1111;
		groupId = 2222;
		companyId = 3333;
		screenName = "screenName1";

		bookmark_1 = new Bookmark();
		bookmark_1.setId(bookmarkId_1);

		Mockito.when(bookmarkRepository.find(bookmarkId_1)).thenReturn(bookmark_1);
	}

	@Test
	public void testAddBookmark() throws CreateBookmarkException {

		bookmarkServiceImpl.addBookmark(bookmark_new);
	}

	@Test
	public void testDeleteBookmarkLong() {

		bookmarkServiceImpl.deleteBookmark(bookmarkId_1);
	}

	@Test
	public void testDeleteBookmarkBookmark() {
		bookmarkServiceImpl.deleteBookmark(bookmark_1);
	}

	@Test
	public void testFind() {
		bookmarkServiceImpl.find(bookmarkId_1);
	}

	@Test
	public void testFindBookmarksByCompanyIdLong() {
		bookmarkServiceImpl.findBookmarksByCompanyId(companyId);
	}

	@Test
	public void testFindBookmarksByCompanyIdLongIntInt() {
		bookmarkServiceImpl.findBookmarksByCompanyId(companyId, 0, 10);
	}

	@Test
	public void testFindBookmarksCountByGroupId() {
		bookmarkServiceImpl.findBookmarksByGroupId(companyId, groupId);
	}

	@Test
	public void testFindBookmarksByGroupIdLongLong() {
		bookmarkServiceImpl.findBookmarksByGroupId(companyId, groupId);
	}

	@Test
	public void testFindBookmarksByGroupIdLongLongIntInt() {
		bookmarkServiceImpl.findBookmarksByGroupId(companyId, groupId, 1, 10);
	}

	@Test
	public void testFindUserBookmarksCount() {
		bookmarkServiceImpl.findUserBookmarks(companyId, screenName, groupId);
	}

	@Test
	public void testFindUserBookmarksLongLongLong() {
		bookmarkServiceImpl.findUserBookmarks(companyId, screenName, groupId);
	}

	@Test
	public void testFindUserBookmarksLongLongLongIntInt() {
		bookmarkServiceImpl.findUserBookmarks(companyId, groupId, screenName, 0, 10);
	}

	@Test
	public void testFindVgrBookmarksForUser() {
		bookmarkServiceImpl.findVgrBookmarksForUser(companyId, screenName);
	}

	@Test
	public void testUpdateBookmarkBookmark() throws UpdateBookmarkException {
		bookmarkServiceImpl.updateBookmark(bookmark_new);

		bookmarkServiceImpl.updateBookmark(bookmark_1);
	}

	@Test
	public void testUpdateBookmarkLongStringStringString() throws UpdateBookmarkException {
		bookmarkServiceImpl.updateBookmark(bookmarkId_1, "title", "url", "description");
	}

}
