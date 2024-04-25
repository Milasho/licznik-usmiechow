package smilecounter.core.data.connectors.mongodb.enums;

public enum MongoFields {
    LOCALISATION("localisation"),
    CREATION_DATE("creationDate"),
    IMAGE("snapshot"),
    FOUND_SMILES("foundSmiles");

    private final String fieldName;

    MongoFields(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getName(){
        return this.fieldName;
    }
}
