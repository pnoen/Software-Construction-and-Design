package oxforddictionaries.model;

import oxforddictionaries.model.request.responseclasses.RetrieveEntry;

import java.util.List;

/**
 * The application has the option to use an online or offline engine for the GET requests.
 */
public interface InputEngine {

    /**
     * Creates the uri and performs a GET request.
     * @param lang language
     * @param word word
     * @param field field
     * @param gramFeat grammatical features
     * @param lexiCate lexical categories
     * @param domains domains
     * @param registers registers
     * @param match match
     * @param newSearch new search
     * @param historyEntry history search
     * @param lemma lemma search
     * @param cacheDecided notified user
     * @param useCache cache or request new data
     * @return list of error messages
     */
    List<String> entrySearch(String lang, String word, String field, String gramFeat, String lexiCate,
                             String domains, String registers, String match, boolean newSearch, boolean historyEntry, boolean lemma,
                             boolean cacheDecided, boolean useCache);

    /**
     * Gets the POJO
     * @return entry
     */
    RetrieveEntry getRetrieveEntry();

    /**
     * Creates the uri and performs a GET request.
     * @param lang language
     * @param word word
     * @param gramFeat grammatical features
     * @param lexiCate lexical categories
     * @param cacheDecided cached been decided
     * @param useCache cache or request new data
     * @return list of error messages
     */
    List<String> lemmaSearch(String lang, String word, String gramFeat, String lexiCate, boolean cacheDecided, boolean useCache);

    /**
     * Contains a list of list with entry information
     * @return history
     */
    List<List<String>> getHistory();

    /**
     * Updates the current page index
     * @param ind page index
     */
    void setCurrentPageInd(int ind);

    /**
     * Finds the lemmas from the POJO
     * @return List of lemmas
     */
    List<List<String>> findLemmas();

    /**
     * Clears the database tables.
     * @return error message
     */
     String clearCache();

    /**
     * Gets the application name from the about data
     * @return application name
     */
     String getAboutAppName();

    /**
     * Gets the developer name from the about data
     * @return developer name
     */
     String getAboutDevName();

    /**
     * Gets the references from the about data
     * @return references
     */
     List<String> getAboutReferences();

    /**
     * Adds pronunciation to the list of pronunciations
     * @param entryId Entry ID
     * @param pronunciation Pronunciation URI
     * @return added or not
     */
    boolean addPronunciation(String entryId, String pronunciation);

    /**
     * Gets the list of pronunciations
     * @return pronunciations
     */
    List<List<String>> getPronunciations();

    /**
     * Removes pronunciation from the list of pronunciations
     * @param pronunciation Pronunciation URI
     * @return removed or not
     */
    boolean removePronunciation(String pronunciation);
}
