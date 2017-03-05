package realnote.designconcept.cloud.classes;

/**
 * Created by ToniApps Studios on 15/11/2016.
 * Visit http://toniapps.es for more info ;)
 */

public class Consts {

    //Online notes storage url
    public static final String URL = "http://realnote.es/realnote/functions.php"; // FIXME: 05/03/2017 Cambiar esto por la dirección del archivo "functioons.php" del server

    //POST vars
    public static final String ACTION_VAR = "action";
    public static final String TITLE_VAR = "title";
    public static final String NOTE_VAR = "note";
    public static final String UID_VAR = "user";
    public static final String DATE_VAR = "date";
    public static final String ID_VAR = "id";

    //POST actions
    public static final String GET_ALL_ACTION = "selectall";
    public static final String GET_BY_ID_ACTION = "select";
    public static final String UPDATE_ACTION = "update";
    public static final String UPDATE_OR_INSERT_ACTION = "updateorinsert";
    public static final String CREATE_ACTION = "new";
    public static final String DELETE_ACTION = "delete";

    //RESPONSES
    public static final String OK_RESPONSE = "ok";

    //JSON RESPONSE VARS
    public static final String ID_JSON_RESPONSE = "id";
    public static final String TITLE_JSON_RESPONSE = "title";
    public static final String NOTE_JSON_RESPONSE = "note";
    public static final String TIMESTAMP_JSON_RESPONSE = "timestamp";


    //Activity Launches VARS
    public static final String NOTE_EXTRA_NAME = "note";


    //Date Format
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:MM:SS";



}
