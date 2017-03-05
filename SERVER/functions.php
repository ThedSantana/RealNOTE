<?php

require "./config.php";


if (!isset($_POST['action'])) {
    echo 'error';
    exit();
} //Si no hay acción denegar la petición

$mysqli = dbConnect(); //Nos conectamos a la DB

$action = $_POST['action']; //La Acción

$title = $_POST['title']; //Título
$note = $_POST['note']; //Nota
$user_id = $_POST['user']; //ID del usuario
$timestamp = $_POST['date']; //Fecha

if (isset($_POST['id'])) { //Si hay ID establecerla
    $id = $_POST['id'];
}


switch ($action) { //Acciones
    case 'selectall':
        $sql = "SELECT * FROM `notes` WHERE `user` = {$user_id} ORDER BY `id` DESC";
        $return = true;
        break;

    case 'select':
        $sql = "SELECT * FROM `notes` WHERE `id` = {$id}";
        $return = true;
        break;

    case 'update':
        $sql = "UPDATE `notes` SET `title`='{$title}',`note`='{$note}',`timestamp`='{$timestamp}' WHERE `id` = {$id}";
        $return = false;
        break;

    case 'new':
        $sql = "INSERT INTO `notes`(`title`, `note`, `user`, `timestamp`) VALUES ('{$title}','{$note}',{$user_id},'{$timestamp}')";
        $return = false;
        $return_id = true;
        break;

    case 'delete':
        $sql = "DELETE FROM `notes` WHERE `id` = {$id}";
        $return = false;
        break;

    case 'updateorinsert':
    	if (isset($id)){
    		$sql = "UPDATE `notes` SET `title`='{$title}',`note`='{$note}',`timestamp`='{$timestamp}' WHERE `id` = {$id}";
    	} else{
    		$sql = "INSERT INTO `notes`(`title`, `note`, `user`, `timestamp`) VALUES ('{$title}','{$note}',{$user_id},'{$timestamp}')";
    	}
        $return = false;
        $return_id = true;
        break;

    default:
        echo 'error';
        exit();
        break;
}

if (!$return) { //Vemos si devolvemos valores o no
    $id = executeSqlWithoutResponse($sql, $mysqli);
    if ($return_id) {
        printf($id);
    }else{
        echo 'ok';
    }

}else{
    $result = executeSql($sql, $mysqli);
    $json = json_encode($result);
    echo $json;
}


$mysqli->close(); //Cerramos la conexión
