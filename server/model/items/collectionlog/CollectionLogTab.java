package server.model.items.collectionlog;

public interface CollectionLogTab {
    String[] getDisplayNames();
    String getIdentifierFor(String displayName);
}