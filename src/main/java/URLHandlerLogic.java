package main.java;

public final class URLHandlerLogic {

	private URLHandlerLogic() {
	}

	public static String getRequestedUrl(String requestUrl, String queryString) {
		if (queryString == null || queryString.isBlank()) {
			return requestUrl;
		}
		return requestUrl + "?" + queryString;
	}
}