package ppss;

import org.dbunit.Assertion;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcBasedDBTestCase;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.util.fileloader.FlatXmlDataFileLoader;

import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.junit.jupiter.api.*;

/* IMPORTANTE:
    Dado que prácticamente todos los métodos de dBUnit lanzan una excepción,
    vamos a usar "throws Esception" en los métodos, para que el código quede más
    legible sin necesidad de usar un try..catch o envolver cada sentencia dbUnit
    con un assertDoesNotThrow()
*/
public class ClienteDAO_IT {

  private ClienteDAO clienteDAO; //SUT
  private IDatabaseTester databaseTester;
  private IDatabaseConnection connection;

  @BeforeEach
  public void setUp() throws Exception {

    String cadena_conexionDB = "jdbc:mysql://localhost:3306/DBUNIT?useSSL=false";
    databaseTester = new MiJdbcDatabaseTester("com.mysql.cj.jdbc.Driver",
            cadena_conexionDB, "root", "ppss");
    //obtenemos la conexión con la BD
    connection = databaseTester.getConnection();

    clienteDAO = new ClienteDAO();
  }

  @Test
  public void testInsert() throws Exception {
    Cliente cliente = new Cliente(1,"John", "Smith");
    cliente.setDireccion("1 Main Street");
    cliente.setCiudad("Anycity");

    //Inicializamos el dataSet con los datos iniciales de la tabla cliente
    IDataSet dataSet = new FlatXmlDataFileLoader().load("/cliente-init.xml");
    //Inyectamos el dataset en el objeto databaseTester
    databaseTester.setDataSet(dataSet);
    //inicializamos la base de datos con los contenidos del dataset
    databaseTester.onSetup();
     //invocamos a nuestro SUT
    Assertions.assertDoesNotThrow(()->clienteDAO.insert(cliente));

    //recuperamos los datos de la BD después de invocar al SUT
    IDataSet databaseDataSet = connection.createDataSet();
    //Recuperamos los datos de la tabla cliente
    ITable actualTable = databaseDataSet.getTable("cliente");

    //creamos el dataset con el resultado esperado
    IDataSet expectedDataSet = new FlatXmlDataFileLoader().load("/cliente-esperado.xml");
    ITable expectedTable = expectedDataSet.getTable("cliente");

    Assertion.assertEquals(expectedTable, actualTable);

   }
  @Test
  public void testDelete() throws Exception {
    Cliente cliente =  new Cliente(1,"John", "Smith");
    cliente.setDireccion("1 Main Street");
    cliente.setCiudad("Anycity");

    //inicializamos la BD
    IDataSet dataSet = new FlatXmlDataFileLoader().load("/cliente-esperado.xml");
    databaseTester.setDataSet(dataSet);
    databaseTester.onSetup();

    //invocamos a nuestro SUT
    Assertions.assertDoesNotThrow(()->clienteDAO.delete(cliente));

    IDataSet databaseDataSet = connection.createDataSet();
    //Recuperamos los datos de la tabla cliente
    ITable actualTable = databaseDataSet.getTable("cliente");

    //creamos el dataset con el resultado esperado
    IDataSet expectedDataSet = new FlatXmlDataFileLoader().load("/cliente-init.xml");
    ITable expectedTable = expectedDataSet.getTable("cliente");

    Assertion.assertEquals(expectedTable, actualTable);
  }

  @Test
  public void test_insert2() throws Exception {
    Cliente cliente = new Cliente(1,"Vadym", "for");
    cliente.setDireccion("sanvi");
    cliente.setCiudad("sanvi");

    //Inicializamos el dataSet con los datos iniciales de la tabla cliente
    IDataSet dataSet = new FlatXmlDataFileLoader().load("/cliente-init.xml");
    //Inyectamos el dataset en el objeto databaseTester
    databaseTester.setDataSet(dataSet);
    //inicializamos la base de datos con los contenidos del dataset
    databaseTester.onSetup();
    //invocamos a nuestro SUT
    Assertions.assertDoesNotThrow(()->clienteDAO.insert(cliente));

    //recuperamos los datos de la BD después de invocar al SUT
    IDataSet databaseDataSet = connection.createDataSet();
    //Recuperamos los datos de la tabla cliente
    ITable actualTable = databaseDataSet.getTable("cliente");

    //creamos el dataset con el resultado esperado
    IDataSet expectedDataSet = new FlatXmlDataFileLoader().load("/cliente-esperado2.xml");
    ITable expectedTable = expectedDataSet.getTable("cliente");

    Assertion.assertEquals(expectedTable, actualTable);
  }
  @Test
  public void test_delete2() throws Exception {
    Cliente cliente =  new Cliente(1,"Vadym", "for");
    cliente.setDireccion("sanvi");
    cliente.setCiudad("sanvi");

    //inicializamos la BD
    IDataSet dataSet = new FlatXmlDataFileLoader().load("/cliente-esperado2.xml");
    databaseTester.setDataSet(dataSet);
    databaseTester.onSetup();

    //invocamos a nuestro SUT
    Assertions.assertDoesNotThrow(()->clienteDAO.delete(cliente));

    IDataSet databaseDataSet = connection.createDataSet();
    //Recuperamos los datos de la tabla cliente
    ITable actualTable = databaseDataSet.getTable("cliente");

    //creamos el dataset con el resultado esperado
    IDataSet expectedDataSet = new FlatXmlDataFileLoader().load("/cliente-init.xml");
    ITable expectedTable = expectedDataSet.getTable("cliente");

    Assertion.assertEquals(expectedTable, actualTable);
  }



  @Test
  public void testUpdate() throws Exception {
    Cliente cliente =  new Cliente(1,"John", "Smith");
    cliente.setDireccion("Other Street");
    cliente.setCiudad("NewCity");

    //inicializamos la BD
    IDataSet dataSet = new FlatXmlDataFileLoader().load("/cliente-esperado.xml"); //cliente inicial
    databaseTester.setDataSet(dataSet);
    databaseTester.onSetup();

    //invocamos a nuestro SUT
    Assertions.assertDoesNotThrow(()->clienteDAO.update(cliente));

    IDataSet databaseDataSet = connection.createDataSet();
    //Recuperamos los datos de la tabla cliente
    ITable actualTable = databaseDataSet.getTable("cliente");

    //creamos el dataset con el resultado esperado
    IDataSet expectedDataSet = new FlatXmlDataFileLoader().load("/cliente-esperado-apartadoD.xml");
    ITable expectedTable = expectedDataSet.getTable("cliente");

    Assertion.assertEquals(expectedTable, actualTable);
  }

  @Test
  public void testRetrieve() throws Exception {
    Cliente clienteEsperado =  new Cliente(1,"John", "Smith");
    clienteEsperado.setDireccion("1 Main Street");
    clienteEsperado.setCiudad("Anycity");

    //inicializamos la BD
    IDataSet dataSet = new FlatXmlDataFileLoader().load("/cliente-esperado.xml");//cliente inicial
    databaseTester.setDataSet(dataSet);
    databaseTester.onSetup();

    //invocamos a nuestro SUT
    int idARecuperar = 1;
    Cliente clienteRecuperado = Assertions.assertDoesNotThrow(()->clienteDAO.retrieve(idARecuperar));

    Assertions.assertAll(
            () -> Assertions.assertEquals(clienteEsperado.getId(), clienteRecuperado.getId()),
            () -> Assertions.assertEquals(clienteEsperado.getNombre(), clienteRecuperado.getNombre()),
            () -> Assertions.assertEquals(clienteEsperado.getApellido(), clienteRecuperado.getApellido()),
            () -> Assertions.assertEquals(clienteEsperado.getDireccion(), clienteRecuperado.getDireccion()),
            () -> Assertions.assertEquals(clienteEsperado.getCiudad(), clienteRecuperado.getCiudad())
    );
  }

}
