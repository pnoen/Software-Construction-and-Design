package oxforddictionaries.model;

import oxforddictionaries.model.request.Request;
import oxforddictionaries.model.request.responseclasses.RetrieveEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class OnlineInputEngineTest {
    private OnlineInputEngine onlineInputEngine;
    private Request requestMock;
    private LemmaProcessor lemmaProcessorMock;

    @BeforeEach
    public void setup() {
        this.requestMock = mock(Request.class);
        this.lemmaProcessorMock = mock(LemmaProcessor.class);
        this.onlineInputEngine = new OnlineInputEngine(requestMock, lemmaProcessorMock);
    }

    @Test
    public void createUriFieldsValidFields() {
        String uri = "uri.test/";
        String newUri = onlineInputEngine.createUriFields(uri, "a", "b", "c,c", "d", "e", "f");
        String expected = "uri.test/?fields=a&grammaticalFeatures=b&lexicalCategory=c,c&domains=d&registers=e&strictMatch=f";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void createUriFieldsNullFields() {
        String uri = "uri.test/";
        String newUri = onlineInputEngine.createUriFields(uri, null, null, null, null, null, null);
        assertThat(newUri, equalTo(uri));
    }

    @Test
    public void createUriFieldsEmptyFields() {
        String uri = "uri.test/";
        String newUri = onlineInputEngine.createUriFields(uri, "a", "", "   ", " ", "", "b");
        String expected = "uri.test/?fields=a&strictMatch=b";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void createUriFieldsLeadingSpaces() {
        String uri = "uri.test/";
        String newUri = onlineInputEngine.createUriFields(uri, " a ", "", "", "", "", "");
        String expected = "uri.test/?fields= a ";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void uriEscapePercentageSymbol() {
        String uri = "uri.test/%new";
        String newUri = onlineInputEngine.uriEscape(uri);
        String expected = "uri.test/%25new";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void uriEscapeDollarSymbol() {
        String uri = "uri.test/$new";
        String newUri = onlineInputEngine.uriEscape(uri);
        String expected = "uri.test/%24new";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void uriEscapeSpace() {
        String uri = "uri.test/ new";
        String newUri = onlineInputEngine.uriEscape(uri);
        String expected = "uri.test/%20new";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void uriEscapeCombination() {
        String uri = "uri.test/% $ new";
        String newUri = onlineInputEngine.uriEscape(uri);
        String expected = "uri.test/%25%20%24%20new";
        assertThat(newUri, equalTo(expected));
    }

    @Test
    public void createHistoryEntryValid() {
        List<String> entry = onlineInputEngine.createHistoryEntry("en-gb", "noun", "", "", "",
                "", "", "", true);
        assertThat(entry.size(), equalTo(9));
        assertThat(entry.get(0), equalTo("en-gb"));
        assertThat(entry.get(1), equalTo("noun"));
    }

    @Test
    public void createHistoryEntryNullEmpty() {
        List<String> entry = onlineInputEngine.createHistoryEntry("en-gb", "noun", null, "", null,
                "", "", "", true);
        assertThat(entry.size(), equalTo(9));
        assertThat(entry.get(2), equalTo(""));
        assertThat(entry.get(3), equalTo(""));
    }

    @Test
    public void createHistoryEntrySpaces() {
        List<String> entry = onlineInputEngine.createHistoryEntry("en-gb", "noun", " ", " s ", null,
                "", "", "", true);
        assertThat(entry.size(), equalTo(9));
        assertThat(entry.get(2), equalTo(" "));
        assertThat(entry.get(3), equalTo(" s "));
    }

    @Test
    public void createHistoryEntryNewSearchTrue() {
        List<String> entry = onlineInputEngine.createHistoryEntry("en-gb", "noun", "", "", null,
                "", "", "", true);
        assertThat(entry.get(8), equalTo("Searched"));
    }

    @Test
    public void createHistoryEntryNewSearchFalse() {
        List<String> entryTrue = onlineInputEngine.createHistoryEntry("en-gb", "noun", "", "", null,
                "", "", "", true);
        onlineInputEngine.getHistory().add(entryTrue);

        List<String> entryFalse = onlineInputEngine.createHistoryEntry("en-gb", "yo", "", "", null,
                "", "", "", false);
        assertThat(entryFalse.get(8), equalTo("Synonym/Antonym of noun"));
    }

    @Test
    public void createHistoryEntrySynonymOfFirstEntry() {
        List<String> entry1 = onlineInputEngine.createHistoryEntry("en-gb", "first", "", "", null,
                "", "", "", true);
        onlineInputEngine.getHistory().add(entry1);

        List<String> entry2 = onlineInputEngine.createHistoryEntry("en-gb", "second", "", "", null,
                "", "", "", true);
        onlineInputEngine.getHistory().add(entry2);

        assertThat(onlineInputEngine.getHistory().get(0), equalTo(entry1));
        assertThat(onlineInputEngine.getHistory().get(1), equalTo(entry2));

        List<String> entry3 = onlineInputEngine.createHistoryEntry("en-gb", "yo", "", "", null,
                "", "", "", false);

        assertThat(entry3.get(8), equalTo("Synonym/Antonym of first"));
    }

    @Test
    public void handleError() {
        String code = "404";
        String body = "{\"error\": \"No entry found matching supplied source_lang, word and provided filters\"}";
        List<String> error = onlineInputEngine.handleErrorReq(code, body);
        assertThat(error.size(), equalTo(2));
        assertThat(error.get(0), equalTo("404"));
        assertThat(error.get(1), equalTo("No entry found matching supplied source_lang, word and provided filters"));
    }

    @Test
    public void handleErrorExtraBody() {
        String code = "401";
        String body = "{\"error\": \"error message\", \"extra\": \"not necessary\"}";
        List<String> error = onlineInputEngine.handleErrorReq(code, body);
        assertThat(error.size(), equalTo(2));
        assertThat(error.get(0), equalTo("401"));
        assertThat(error.get(1), equalTo("error message"));
    }

    @Test
    public void entrySearchValid() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, false);
        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(1));
        assertThat(onlineInputEngine.getHistory().get(0).get(1), equalTo("noun"));

        RetrieveEntry retrieveEntry = onlineInputEngine.getRetrieveEntry();
        assertThat(retrieveEntry.getMetadata().getSchema(), equalTo("entry"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchNull() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", null, "", null,
                "", "", "", true, false, false);
        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(1));
        assertThat(onlineInputEngine.getHistory().get(0).get(1), equalTo("noun"));

        RetrieveEntry retrieveEntry = onlineInputEngine.getRetrieveEntry();
        assertThat(retrieveEntry.getMetadata().getSchema(), equalTo("entry"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchSpaces() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "  ", "  ", "",
                " ", "", "", true, false, false);
        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(1));
        assertThat(onlineInputEngine.getHistory().get(0).get(1), equalTo("noun"));

        RetrieveEntry retrieveEntry = onlineInputEngine.getRetrieveEntry();
        assertThat(retrieveEntry.getMetadata().getSchema(), equalTo("entry"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchExceptionCaused() {
        List<String> response = new ArrayList<>();
        response.add("Caught some exception here");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, false);
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.get(0), equalTo("Caught some exception here"));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(0));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchResponseError() {
        List<String> response = new ArrayList<>();
        response.add("400");
        response.add("{\"error\": \"error body\"}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, false);
        assertThat(actual.size(), equalTo(2));
        assertThat(actual.get(0), equalTo("400"));
        assertThat(actual.get(1), equalTo("error body"));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(0));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchNoResultsFound() {
        List<String> response = new ArrayList<>();
        response.add("404");
        response.add("{\"error\": \"no entry found\"}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, false);
        assertThat(actual, is(nullValue()));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(0));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchNoResultsFoundLemma() {
        List<String> response = new ArrayList<>();
        response.add("404");
        response.add("{\"error\": \"no entry found\"}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, true);
        assertThat(actual.size(), equalTo(2));
        assertThat(actual.get(0), equalTo("404"));
        assertThat(actual.get(1), equalTo("no entry found"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchMultipleSearches() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, false);

        response.clear();
        response.add("200");
        response.add("{\"id\": \"donkey\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);
        List<String> actual = onlineInputEngine.entrySearch("en-gb", "donkey", "", "", "",
                "", "", "", true, false, false);

        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(2));
        assertThat(onlineInputEngine.getHistory().get(1).get(1), equalTo("donkey"));
        assertThat(onlineInputEngine.getCurrentPageInd(), equalTo(1));

        verify(requestMock, times(2)).getRequest(anyString());
    }

    @Test
    public void entrySearchHistorySearchNewSearch() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, true, false);

        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(0));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchHistorySearchNotNewSearch() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", false, true, false);

        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(0));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/entries/en-gb/noun");
    }

    @Test
    public void entrySearchNotNewSearch() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"id\": \"noun\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        onlineInputEngine.entrySearch("en-gb", "noun", "", "", "",
                "", "", "", true, false, false);

        response.clear();
        response.add("200");
        response.add("{\"id\": \"donkey\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        onlineInputEngine.entrySearch("en-gb", "donkey", "", "", "",
                "", "", "", true, false, false);

        assertThat(onlineInputEngine.getHistory().get(1).get(1), equalTo("donkey"));
        assertThat(onlineInputEngine.getCurrentPageInd(), equalTo(1));
        onlineInputEngine.setCurrentPageInd(0);

        response.clear();
        response.add("200");
        response.add("{\"id\": \"cow\",\"metadata\": {\"operation\": \"retrieve\",\"provider\": \"Oxford University Press\",\"schema\": \"entry\"}}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.entrySearch("en-gb", "cow", "", "", "",
                "", "", "", false, false, false);

        assertThat(actual.size(), equalTo(0));
        assertThat(onlineInputEngine.getHistory().size(), equalTo(3));
        assertThat(onlineInputEngine.getHistory().get(0).get(1), equalTo("donkey"));
        assertThat(onlineInputEngine.getHistory().get(1).get(1), equalTo("noun"));
        assertThat(onlineInputEngine.getCurrentPageInd(), equalTo(2));

        verify(requestMock, times(3)).getRequest(anyString());
    }

    @Test
    public void lemmaSearchValid() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"metadata\": {\"provider\": \"Oxford\"},\"results\": [{\"id\": \"forehead\",\"language\": \"en\"}]}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.lemmaSearch("en", "forehead", "", "");
        assertThat(actual.size(), equalTo(0));

        RetrieveEntry retrieveEntry = onlineInputEngine.getRetrieveEntry();
        assertThat(retrieveEntry.getResults().get(0).getId(), equalTo("forehead"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/lemmas/en/forehead");
    }

    @Test
    public void lemmaSearchNull() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"metadata\": {\"provider\": \"Oxford\"},\"results\": [{\"id\": \"forehead\",\"language\": \"en\"}]}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.lemmaSearch("en", "forehead", null, null);
        assertThat(actual.size(), equalTo(0));

        RetrieveEntry retrieveEntry = onlineInputEngine.getRetrieveEntry();
        assertThat(retrieveEntry.getResults().get(0).getId(), equalTo("forehead"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/lemmas/en/forehead");
    }

    @Test
    public void lemmaSearchSpaces() {
        List<String> response = new ArrayList<>();
        response.add("200");
        response.add("{\"metadata\": {\"provider\": \"Oxford\"},\"results\": [{\"id\": \"forehead\",\"language\": \"en\"}]}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.lemmaSearch("en", "forehead", "  ", " ");
        assertThat(actual.size(), equalTo(0));

        RetrieveEntry retrieveEntry = onlineInputEngine.getRetrieveEntry();
        assertThat(retrieveEntry.getResults().get(0).getId(), equalTo("forehead"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/lemmas/en/forehead");
    }

    @Test
    public void lemmaSearchExceptionCaused() {
        List<String> response = new ArrayList<>();
        response.add("Exception caught somewhere here");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.lemmaSearch("en", "forehead", "", "");
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.get(0), equalTo("Exception caught somewhere here"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/lemmas/en/forehead");
    }

    @Test
    public void lemmaSearchErrorCode() {
        List<String> response = new ArrayList<>();
        response.add("400");
        response.add("{\"error\": \"no lemmas found\"}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.lemmaSearch("en", "forehead", "", "");
        assertThat(actual.size(), equalTo(2));
        assertThat(actual.get(0), equalTo("400"));
        assertThat(actual.get(1), equalTo("no lemmas found"));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/lemmas/en/forehead");
    }

    @Test
    public void lemmaSearchErrorCode404() {
        List<String> response = new ArrayList<>();
        response.add("404");
        response.add("{\"error\": \"no lemmas found\"}");
        when(requestMock.getRequest(anyString())).thenReturn(response);

        List<String> actual = onlineInputEngine.lemmaSearch("en", "forehead", "", "");
        assertThat(actual, is(nullValue()));

        verify(requestMock, times(1)).getRequest("https://od-api.oxforddictionaries.com/api/v2/lemmas/en/forehead");
    }
}