package es;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.conversions.Bson;


public class App
{
    public static void main( String[] args ) throws ParseException {
        MongoClient mongoClient = new MongoClient();
        MongoDatabase db = getDB("localhost", 27017, "cliente");
        Scanner lector = new Scanner(System.in);
        String opc = "";
        MongoCollection<Document> collection = db.getCollection("cliente");
        //llamadas totales recibidas

        do {
            System.out.println("------------------------------------------------------------");
            System.out.println("Llamadas totales recibidas: " + collection.countDocuments());
            //Mostrar problemas de hardware
            System.out.println("Problemas de hardware: " + collection.countDocuments(new Document("llamada.problema", "hardware")));
            //Mostrar problemas de software
            System.out.println("Problemas de software: " + collection.countDocuments(new Document("llamada.problema", "software")));
            //Mostrar problemas solucionados
            System.out.println("Problemas solucionados: " + collection.countDocuments(new Document("llamada.problemasolucionado", "si")));

            System.out.println("1. Añadir.");
            System.out.println("2. Listado.");
            System.out.println("3. Modificar.");
            System.out.println("4. Eliminar.");
            System.out.println("5. Terminar.");
            System.out.println("¿Qué opción eliges?");
            opc = lector.nextLine();
            System.out.println("------------------------------------------------------------");
            switch (opc) {
                case "1":
                    FindIterable<Document> resultDocument2 = collection.find();
                    Document incidencia = new Document();
                    //Extraer el ultimo id añadiendole 1
                    int id = Integer.parseInt((String) resultDocument2.sort(new Document("_id", -1)).first().get("_id")) + 1;
                    String id2 = String.valueOf(id);
                    incidencia.put("_id", id2);
                    System.out.println("Introduce su nombre:");
                    incidencia.append("nombre", lector.nextLine());
                    System.out.println("Introduce su apellido del cliente:");
                    incidencia.append("apellidos", lector.nextLine());
                    int terminal = (int) (Math.random() * 10 + 1);
                    String term = String.valueOf(terminal);
                    incidencia.append("terminal", term);
                    System.out.println("Introduce el motivo de la llamada:");
                    String motivo = lector.nextLine();
                    System.out.println("Introduce el problema (software/hardware):");
                    String problema = lector.nextLine();
                    System.out.println("Introduce si necesita reparacion (si/no):");
                    String reparacion = lector.nextLine();
                    System.out.println("Introduce si se ha solucionado el problema (si/no):");
                    String solucion = lector.nextLine();
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat(
                            "dd-MM-yyyy");
                    formatter.parse(formatter.format(new Date()));
                    System.out.println("Fecha de la llamada " + formatter.format(date));
                    String fecha = formatter.format(date);
                    incidencia.append("llamada", new Document("fechallamada", fecha).append("motivo", motivo).append("problema", problema).append("reparacion", reparacion).append("problemasolucionado", solucion));

                    collection.insertOne(incidencia);
                    break;
                case "2":
                    MongoCollection col = db.getCollection("cliente");
                    // Opcion seleccionar un cliente o mostrarlos todos
                    System.out.println("1. Mostrar todos los clientes.");
                    System.out.println("2. Seleccionar un cliente.");
                    System.out.println("¿Qué opción eliges?");
                    String opc2 = lector.nextLine();
                    switch (opc2) {
                        case "1":
                            // Document to store query results
                            FindIterable<Document> resultDocument1 = col.find();
                            // Return all documents in the collection
                            for (Document document : resultDocument1) {
                                for (String key : document.keySet()) {
                                    System.out.println(key + " : " + document.get(key));
                                }
                            }
                            break;
                        case "2":
                            System.out.println("Dime el nombre del cliente:");
                            String nombre = lector.nextLine();
                            // Create the document to specify find criteria
                            Document findDocument = new Document("nombre", nombre);
                            System.out.println("Numero de llamadas del cliente: " + col.countDocuments(findDocument));
                            // Document to store query results
                            FindIterable<Document> resultDocument = col.find(findDocument);

                            // Return all documents in the collection
                            for (Document document : resultDocument) {
                                for (String key : document.keySet()) {
                                    System.out.println(key + " : " + document.get(key));
                                }
                            }
                    }

                    break;
                case "3":
                    System.out.println("Introduce el id del cliente que quieres modificar:");
                    String id1 = lector.nextLine();
                    // Update problemasolucionado
                    Document findDocument1 = new Document("_id", id1);
                    System.out.println("Introduce si se ha solucionado el problema (si/no):");
                    String solucion1 = lector.nextLine();
                    Document updateDocument1 = new Document("llamada.problemasolucionado", solucion1);
                    Document setDocument1 = new Document("$set", updateDocument1);
                    collection.findOneAndUpdate(findDocument1, setDocument1);

                    break;
                case "4":
                    System.out.println("Introduce el id del cliente que quieres eliminar:");
                    String id3 = lector.nextLine();
                    Document findDocuments = new Document("_id", id3);
                    collection.findOneAndDelete(findDocuments);
                    break;
                case "5":
                    System.out.println("Hasta pronto!");
                    disconnect(mongoClient);
                    break;
                default:
                    System.out.println("Opción incorrecta");
                    break;
            }
        }
        while (!opc.equals("5"));
    }

    public static MongoDatabase getDB(String host, int port, String database) {
        MongoClient mongoClient = new MongoClient(host, port);
        MongoDatabase db = mongoClient.getDatabase(database);
        System.out.println( "Iniciando Mongo" );
//        List<String> dbs = mongoClient.getDatabase(database).listCollectionNames().into(new ArrayList<String>());
//        System.out.println(dbs);
        return db;
    }
    public static void disconnect(MongoClient mongoClient) {
        System.out.println( "Cerrando Mongo" );
        mongoClient.close();
    }

}
