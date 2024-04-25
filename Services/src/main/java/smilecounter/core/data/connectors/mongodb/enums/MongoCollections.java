package smilecounter.core.data.connectors.mongodb.enums;

public enum MongoCollections {
    SMILES("smiles"),
    TESTS("tests");

    private final String smiles;

    MongoCollections(String smiles) {
        this.smiles = smiles;
    }

    public String getName(){
        return this.smiles;
    }
}
