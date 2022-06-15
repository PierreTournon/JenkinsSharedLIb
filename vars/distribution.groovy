/**
*Associe les esclaves aux noms des tests qu'ils doivent exécuter selon un algorithme de distribution.
*
*@param testNames Tableau des noms de test à associer.
*@param slavesAvailable Tableau des noms d'esclave disponibles.
*@return distribution HashMap associant chaque nom d'esclave aux noms des tests qu'il doit effectuer.
*/
Map<String, List<String>> distribute (String[] testNames, String[] slavesAvailable) {
    
    Map<String, List<String>> distribution = new HashMap<>();
    
    //On associe des tests à des esclaves si ces deux derniers ne sont pas vides 
    if(testNames.length > 0 && slavesAvailable.length > 0) {
        for(String slave in slavesAvailable) {
            distribution.put(slave, new ArrayList<String>());
        }
        
        int nbTests = testNames.length / slavesAvailable.length;
        int lastTests = testNames.length % slavesAvailable.length;
        
        int keyIndex = 0;
        int testNameIndex = 0;
        for(String k in distribution.keySet()) {
            //Dans le cas où le nombre de tests n'est pas divisible par le nombre d'esclaves dispos, 
            //on ajoute le reste des tests au premier esclave dispo
            if(keyIndex == 0) {
                for(int i = 0; i < lastTests; i++) {
                    distribution.get(k).add(testNames[testNameIndex]);
                    testNameIndex++;
                }
            }
            for(int i = 0; i < nbTests; i++) {
                distribution.get(k).add(testNames[testNameIndex]);
                testNameIndex++;
            }
            keyIndex++;
        }
        
        //Affichage
        for(String k in distribution.keySet()) {
            println("Key : "+k);
            for(String n in distribution.get(k)) {
                println("Nom test associé : "+n);
            }
        }
    }

    return distribution;
}

/**
*Cherche, dans le réservoir d'esclaves du serveur Jenkins, les esclaves disponibles pour exécuter des tests.
*
*@return freeNodes Tableau des noms d'esclaves disponibles pour exécuter des tests.
*/
String[] slavesReady () {
    String[] freeNodes = [];
    //On recherche noeud qui soit en ligne parmis la liste de noeud dont dispose notre serveur Jenkins 
    for(Node node in jenkins.model.Jenkins.instance.nodes) {
        if(node.toComputer().isOnline()) {
            freeNodes += node.getNodeName();
            println("Nom esclave en ligne : "+node.getNodeName());
        }
    }
    return freeNodes;
}