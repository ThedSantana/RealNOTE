<?php


  define('SK', "6mÓe7fxháÑCTKraÚp1HzbWdOLXRvúñsD5iSAoükNcygéuÁóZ lGí3w8Ü0EJnQFU2ÍBqPj4t9MVÉYI"); //Clave de seguridad
  define('HOST', "TÚÓ6ThÚzb"); //HOST codificado
  define('USER', "H76TrÚb7_e7f6WTb"); //Usuario codificado
  define('PASS', "AjV0MLÁO.RRt"); //Contraseña codificada
  define('DB', "H76TrÚb7_rÚb7z"); //Base de datos codificada

  function decode($value = '', $sk = SK)
  {
      $dictionary = array(
          'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'ñ', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
          'w', 'x', 'y', 'z', 'á', 'é', 'í', 'ó', 'ú', 'ü', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'Ñ',
          'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Á', 'É', 'Í', 'Ó', 'Ú', 'Ü', '1', '2', '3', '4', '5', '6', '7',
          '8', '9', '0', ' ',
        );
      $sk_splited = preg_split('/(?!^)(?=.)/u', $sk);
      $value_splited = preg_split('/(?!^)(?=.)/u', $value);
      $result = '';
      foreach ($value_splited as $value) {
          if (in_array($value, $sk_splited)) {
              $number = array_search($value, $sk_splited, true);
              $result .= $dictionary[$number];
          } else {
              $result .= $value;
          }
      }
      return $result;
  }

  function encode($value = '', $sk = SK)
  {
      $dictionary = array(
          'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'ñ', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v',
          'w', 'x', 'y', 'z', 'á', 'é', 'í', 'ó', 'ú', 'ü', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'Ñ',
          'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'Á', 'É', 'Í', 'Ó', 'Ú', 'Ü', '1', '2', '3', '4', '5', '6', '7',
          '8', '9', '0', ' ',
        );
      $sk_splited = preg_split('/(?!^)(?=.)/u', $sk);
      $value_splited = preg_split('/(?!^)(?=.)/u', $value);
      $result = '';
      foreach ($value_splited as $value) {
          if (in_array($value, $sk_splited)) {
              $number = array_search("$value", $dictionary, true);
              $result .= $sk_splited[$number];
          } else {
              $result .= $value;
          }
      }

      return "$result";
  }

  function dbConnect()
  {
      $mysqli = new mysqli(decode(HOST), decode(USER), decode(PASS), decode(DB));
      if ($mysqli->connect_errno) {
          return false;
      }

      return $mysqli;
  }

  function executeSql($sql, $mysqli)
  {
      if (!$resultado = $mysqli->query($sql)) {
          return false;
      }
      if ($resultado->num_rows === 0) {
          return false;
      }
      $results = array();
      while ($result = $resultado->fetch_assoc()) {
          array_push($results, $result);
      }

      return $results;
  }
  function executeSqlWithoutResponse($sql, $mysqli)
  {
        mysqli_query($mysqli, $sql);
        $result = mysqli_insert_id($mysqli);
        return $result;
  }
