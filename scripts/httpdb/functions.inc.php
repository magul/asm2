<?php 
	
	// For Error Logging		
	//Example usage:
	//    $data = array('first', 'second', 'third');
	//    $result = varDumpToString($data);
	//
	function varDumpToString ($var)
	{
		ob_start();
		var_dump($var);
		$result = ob_get_clean();
		return $result;
	}

	function startsWith($haystack, $needle)
	{
		$length = strlen($needle);	
		return (substr($haystack, 0, $length) === $needle);	
	} // -- End function startsWith -- //	

	function tokenise($str)
	{
		$s = str_replace("\n", "\\lf", $str);
		$s = str_replace("\r", "\\cr", $s);
		return $s;
	} // -- End function tokenise -- //	

	function map_type($s)
	{
		# Maps the database types to integer, timestamp, varchar or float
                # MySQL types
                if ($s == 1 || $s == 2 || $s == 3 || $s == 8 || $s == 9) { return "integer";}
                if ($s == 4 || $s == 246 || $s == 5) { return "float"; }
                if ($s == 10 || $s == 12 || $s == 7) { return "timestamp"; }
                return "varchar"; 
	} // -- End function map_type -- //	
	
	
?>
