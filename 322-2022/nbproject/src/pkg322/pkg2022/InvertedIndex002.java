package pkg322.pkg2022;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author ehab
 */
/*
 * InvertedIndex - Given a set of text files, implement a program to create an 
 * inverted index. Also create a user interface to do a search using that inverted 
 * index which returns a list of files that contain the query term / terms.
 * The search index can be in memory. 
 * 

 */
import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry2 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry2() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index2 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry2> index; // THe inverted index
    //--------------------------------------------

    Index2() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry2>();
    }

    //---------------------------------------------
    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry2 dd = (DictEntry2) pair.getValue();
            HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--)))> ");

            Iterator<Integer> it2 = hset.iterator();
            while (it2.hasNext()) {
                System.out.print(it2.next() + ", ");
            }
            System.out.println("");
            //it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size());
    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry2());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term 
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }
                printDictionary();
            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
    }

    //--------------------------------------------------------------------------
    // query inverted index
    // takes a string of terms as an argument
    public String find(String phrase) {
        String[] words = phrase.split("\\W+");
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        for (String word : words) {
            res.retainAll(index.get(word).postingList);
        }
        if (res.size() == 0) {
            System.out.println("Not found");
            return "";
        }
        String result = "Found in: \n";
        for (int num : res) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }


    //----------------------------------------------------------------------------
    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = 0, docId2 = 0;
//        INTERSECT ( p1 , p2 )
//          1 answer ←   {}
        // answer =
//          2 while p1  != NIL and p2  != NIL
        if (itP1.hasNext())
            docId1 = itP1.next();
        if (itP2.hasNext())
            docId2 = itP2.next();

        while (itP1.hasNext() && itP2.hasNext()) {

//          3 do if docID ( p 1 ) = docID ( p2 )
            if (docId1 == docId2) {
//          4   then ADD ( answer, docID ( p1 ))
//          5       p1 ← next ( p1 )
//          6       p2 ← next ( p2 )
                answer.add(docId1);
                docId1 = itP1.next();
                docId2 = itP2.next();
            } //          7   else if docID ( p1 ) < docID ( p2 )
            //          8        then p1 ← next ( p1 )
            else if (docId1 < docId2) {
                if (itP1.hasNext())
                    docId1 = itP1.next();
                else return answer;

            } else {
//          9        else p2 ← next ( p2 )
                if (itP2.hasNext())
                    docId2 = itP2.next();
                else return answer;

            }

        }
        if (docId1 == docId2) {
            answer.add(docId1);
        }

//          10 return answer
        return answer;
    }
    //-----------------------------------------------------------------------

    HashSet<Integer> OR (HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer> answer = new HashSet<Integer>();
//        Set<int> hash_Set = new HashSet<int>();
        Iterator<Integer> itP1 = pL1.iterator();
        Iterator<Integer> itP2 = pL2.iterator();
        int docId1 = 0, docId2 = 0;
//


        while (itP1.hasNext() || itP2.hasNext()) {


            if (itP1.hasNext()){
                answer.add(docId1);
                docId1 = itP1.next();}

            if (itP2.hasNext()){
                answer.add(docId2);
                docId2 = itP2.next();
                }

        }
        answer.add(docId1);
        answer.add(docId2);

//          10 return answer
        return answer;
    }


    //-----------------------------------------------------------------------

    HashSet<Integer> NOT (HashSet<Integer> pL1) {
        HashSet<Integer> documents = new HashSet<Integer>();
        for (int i = 0; i < 10; i++)
        {documents.add(i);}

//        HashSet<Integer> answer = new HashSet<Integer>();

        Iterator<Integer> itP1 = pL1.iterator();

        int docId1 = -1;

        while (itP1.hasNext() ) {
            documents.remove(docId1);
            docId1 = itP1.next();
        }
        documents.remove(docId1);

        return documents;
    }

//-----------------------------------------------------------------------

    public String find_OR_NOT(String phrase) { // 2 term phrase  2 postingsLists
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[words.length-1].toLowerCase()).postingList);
        HashSet<Integer> answer = NOT(pL2);
        HashSet<Integer> answer_2 = OR(pL1, answer);
        if (answer_2.size() == 0) {
            System.out.println("Not found");
            return "";
        }
        System.out.println("Found in: ");
        for (int num : answer_2) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }

//-----------------------------------------------------------------------
public String find_AND_NOT(String phrase) { // 2 term phrase  2 postingsLists
    String result = "";
    String[] words = phrase.split("\\W+");
    // 1- get first posting list
    HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
    // 2- get second posting list
    HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[words.length-1].toLowerCase()).postingList);
    HashSet<Integer> answer = NOT(pL2);
    HashSet<Integer> answer_2 = intersect(pL1, answer);
    if (answer_2.size() == 0) {
        System.out.println("Not found");
        return "";
    }
    System.out.println("Found in: ");
    for (int num : answer_2) {
        //System.out.println("\t" + sources.get(num));
        result += "\t" + sources.get(num) + "\n";
    }
    return result;
}
    //-----------------------------------------------------------------------
    public String Multi_OR(String phrase){
    String result = "";
    String[] words = phrase.split("\\W+");
    HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
    HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
    HashSet<Integer> answer = OR(pL1, pL2);
        for (int i = 4; i < words.length; i=i+2) {
        HashSet<Integer> pL3=new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
        answer = OR(answer,pL3);
    }
        System.out.println("Found in Multi_OR: ");
        for (int num : answer) {
        result += "\t" + sources.get(num) + "\n";
    }
        return result;}
    //-----------------------------------------------------------------------
    public String Multi_AND(String phrase){
        String result = "";
        String[] words = phrase.split("\\W+");
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[2].toLowerCase()).postingList);
        HashSet<Integer> answer = intersect(pL1, pL2);
        for (int i = 4; i < words.length; i=i+2) {
            HashSet<Integer> pL3=new HashSet<Integer>(index.get(words[i].toLowerCase()).postingList);
            answer = intersect(answer,pL3);
        }
        System.out.println("Found in Multi_AND: ");
        for (int num : answer) {
            result += "\t" + sources.get(num) + "\n";
        }
        return result;}
    //-----------------------------------------------------------------------
    public String find_01(String phrase) { // any mumber of terms non-optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[words.length-1].toLowerCase()).postingList);
        // 3- apply the algorithm

        String AND = "AND";
        String OR = "OR";
        String NOT = "NOT";

        if (phrase.toLowerCase().indexOf(OR.toLowerCase()) != -1 && phrase.toLowerCase().indexOf(NOT.toLowerCase()) != -1)
        {
//            if (phrase.toLowerCase().indexOf(NOT.toLowerCase()) != -1)
//            {
            System.out.println(find_OR_NOT(phrase));
//            }
        }
        else if (phrase.toLowerCase().indexOf(AND.toLowerCase()) != -1 && phrase.toLowerCase().indexOf(NOT.toLowerCase()) != -1)
        {
//            if (phrase.toLowerCase().indexOf(NOT.toLowerCase()) != -1)
//            {
            System.out.println(find_AND_NOT(phrase));
//            }
        }

        else if(phrase.toLowerCase().indexOf(AND.toLowerCase()) != -1 ) {


                HashSet<Integer> answer = intersect(pL1, pL2);
                System.out.println("Found in intersect: ");
                for (int num : answer) {
                    //System.out.println("\t" + sources.get(num));
                    result += "\t" + sources.get(num) + "\n";
                }
            }

        else if (phrase.toLowerCase().indexOf(OR.toLowerCase()) != -1)
        {
                System.out.println("Found in OR: ");
                HashSet<Integer> answer = OR(pL1, pL2);
                for (int num : answer) {
                    //System.out.println("\t" + sources.get(num));
                    result += "\t" + sources.get(num) + "\n";
                }
        }
        else if (phrase.toLowerCase().indexOf(NOT.toLowerCase()) != -1)
        {
            HashSet<Integer> answer = NOT(pL2);
            System.out.println("Found in NOT: ");
            for (int num : answer) {
                //System.out.println("\t" + sources.get(num));
                result += "\t" + sources.get(num) + "\n";
            }
        }

        else
            System.out.println("string not found");


        return result;

    }
    //-----------------------------------------------------------------------
    public String find_Multi(String phrase) {
        String result = "";
        String[] words = phrase.split("\\W+");
        // 1- get first posting list
        HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        // 2- get second posting list
        HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[words.length-1].toLowerCase()).postingList);
        // 3- apply the algorithm

        String AND = "AND";
        String OR = "OR";
        String NOT = "NOT";

       if(phrase.toLowerCase().indexOf(AND.toLowerCase()) != -1 ) {

           System.out.println(Multi_AND(phrase));
        }

        else if (phrase.toLowerCase().indexOf(OR.toLowerCase()) != -1)
        {
            System.out.println(Multi_OR(phrase));
        }


        else
            System.out.println("string not found");


        return result;
    }
    //-----------------------------------------------------------------------

//    public void compare(String phrase) { // optimized search
//        long iterations=1000000;
//        String result = "";
//        long startTime = System.currentTimeMillis();
//        for (long i = 1; i < iterations; i++) {
//            result = find(phrase);
//        }
//        long estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.println(" (*) elapsed = " + estimatedTime+" ms.");
//  //----------------------------- -----------------------------
//        System.out.println(" result = " + result);
//        startTime = System.currentTimeMillis();
//        for (long i = 1; i < iterations; i++) {
//            result = find_01(phrase);
//        }
//        estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.println(" (*) Find_03 non-optimized intersect  elapsed = " + estimatedTime +" ms.");
//        System.out.println(" result = " + result);
// //-----------------------------
//        startTime = System.currentTimeMillis();
//        for (long i = 1; i < iterations; i++) {
////            result = find_04(phrase);
//        }
//        estimatedTime = System.currentTimeMillis() - startTime;
//        System.out.println(" (*) Find_04 optimized intersect elapsed = " + estimatedTime+" ms.");
//        System.out.println(" result = " + result);
//    }
}

//=====================================================================
public class InvertedIndex002 {

    public static void main(String args[]) throws IOException {
        Index2 index = new Index2();


        //-----------------------ONE--------------------------
        // can    0, 1, 2, 3, 4, 7, 8, 9                     -
        // computing 2, 4, 5, 6, 7, 8, 9,                    -
        //not computing 0,1,3                                -
        //can AND NOT computing ---->0, 1,3                  -
        //can OR NOT computing 0, 1, 2, 3, 4, 7, 8, 9        -
        //----------------------------------------------------


        String phrase_1 ="followed AND despite";

        String phrase_2="followed OR despite";

        String phrase_3="can AND NOT computing";

        String phrase_4="can OR NOT computing";



        //-----------------------TWO--------------------------
        // category       2, 7,
        // response       7
        // independently  7, 9,
        // category OR response OR independently 0 2 7 9
        // category AND response AND independently 7


        // engineering  0, 1, 7,
        // software     0, 1, 7, 8, 9,  **NOT
        // interplay    0
        // engineering AND software AND interplay 0
        //----------------------------------------------------
        index.buildIndex(new String[]{
//                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\100.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
//                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\101.txt",
//                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\102.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\103.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\104.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\105.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\106.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\107.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\108.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\docs\\109.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\Descation_docs\\100.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\Descation_docs\\101.txt",
                "E:\\Level_4\\second term\\IR\\ass\\ass_1\\Descation_docs\\102.txt"

        });

        do {

            Scanner opj1 = new Scanner(System.in);  // Create a Scanner object
            System.out.println("Enter your chose:");
            String var = opj1.nextLine();  // Read user input
            System.out.println("your chose is: " + var);  // Output user input

            if (var.equals("one") )
            {
                Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Enter search phrase:");

                String search_phrase = myObj.nextLine();  // Read user input

                System.out.println(index.find_01(search_phrase));


            }
            else if(var.equals("multi"))
            {
                Scanner myObj = new Scanner(System.in);  // Create a Scanner object
                System.out.println("Enter search phrase:");
                String search_phrase = myObj.nextLine();  // Read user input
                System.out.println(index.find_Multi(search_phrase));

            }


        } while (!phrase_1.isEmpty());

  }
}

