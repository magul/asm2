<?php

// Include some other functions
include ('functions.inc.php');
// Include PEAR::Log for logging.
// Currently set to log anything PEAR_LOG_WARNING and Above
// include("Log.php");
	
if (PHP_SAPI === 'cli') 
{ 
   // ... 
   echo "To be used on webserver for database interogation \n";
   exit (1);
} else {
	
	# Modify for your backend database
	$dbtype = "MYSQL"; # Should be MYSQL or POSTGRESQL
	$host = "127.0.0.1";	# localhost address
	$username = "pawshoof_admin";
	$password = "info#456";
	$database = "pawshoof_asm";
	$port = '5432';
	$dlogfile = dirname($_SERVER[SCRIPT_FILENAME]) . '/httpdb.log';	
	
	// create Log object
	/*
	*	PEAR_LOG_EMERG   => 'emergency',
	*	PEAR_LOG_ALERT   => 'alert',
	*	PEAR_LOG_CRIT    => 'critical',
	*	PEAR_LOG_ERR     => 'error',
	*	PEAR_LOG_WARNING => 'warning',
	*	PEAR_LOG_NOTICE  => 'notice',
	*	PEAR_LOG_INFO    => 'info',
	*	PEAR_LOG_DEBUG   => 'debug'
	*/
		
	//$log = &Log::singleton("file", $dlogfile, NULL, array(), PEAR_LOG_WARNING);
	
	# Get the database connection
	if ( $dbtype == "MYSQL" )
	{	
		$dbh = new mysqli($host, $username, $password, $database);
		//$log->log("%{file} DbType Setting is $dbtype", PEAR_LOG_INFO);
	}// -- End If -- //
	if ( $dbtype == "POSTGRESQL" )
	{
		$connectionStr = "host=$host port=$port dbname=$database user=$username password=$password connect_timeout=5";
		$dbconn = pg_connect($connectionStr);
		//$log->log("DbType Setting is $dbtype", PEAR_LOG_INFO);		
	}// -- End If -- //
	
	header("Content-Type: text/plain");
	print("\n");

	if ($_SERVER['REQUEST_METHOD'] == 'POST') {
		// …
		$query = $_POST["sql"];
		//$log->log("Using POST", PEAR_LOG_INFO);
	} else {
		// …
		$query = $_GET["sql"];		
		//$log->log("Using GET", PEAR_LOG_INFO);
	} // -- End If/Else -- //
	
	// Remove Slashes from the Query, we dont need them now.
	$query = str_replace("\'","'",$query);

	if ( startsWith( strtolower($query), "httpdb" ) || startsWith( strtolower($query), "dbname" ) )
	{
		print "COLdbname\\typevarchar\n";
		print "ROW" . $dbtype;		
		//$log->log("DbType is $dbtype", PEAR_LOG_INFO);
		
	} elseif ( ! startsWith( strtolower($query), "select" ) ) {
		
		
		$queries = explode(";;",$query);		
		foreach ( $queries as $singleQuery )
		{			
			//$log->log("Query is ==> " . $singleQuery, PEAR_LOG_INFO);
			if ( ! $sth = $dbh->query($singleQuery) )
			{	
				//is_array($queries) ? $log->log("QUERIES [ARRAY] - Failed ==> " . varDumpToString($queries), PEAR_LOG_ERR) : $log->log("QUERY - Failed ==> " . $singleQuery, PEAR_LOG_ERR);
			} // -- End If -- //
		} // -- End Foreach-- //
	} else {
		# We have a resultset type query =====
		# Grab a connection and cursor	
		if ( $sth = $dbh->query($query) )
		{
			$i = 0;			
			$cols = array();
			$queryfc = $sth->field_count;
			
			while ($i < $queryfc )
			{
				$finfo = $sth->fetch_field();
				array_push($cols, array((string)$finfo->name, map_type((string)$finfo->type)) );			
				$i++;
			} // -- End While -- //

			$colmap = 'COL';			
			for ( $x = 0; $x < sizeof($cols); $x++)
			{				
				if ( strlen($colmap) > 3 )
				{
					$colmap .= "\\col";					
				} // -- End For -- //
				$colmap .= $cols[$x][0] . "\\type" . $cols[$x][1];
			} // -- End For -- //
			echo $colmap . "\n";
			
			while ( $row = $sth->fetch_row() )
			{
				$r = array();
				for ( $z = 0; $z < $queryfc; $z++)
				{
					$fdata = $row[$z];
					$contents = varDumpToString($fdata);					
					//$log->log("Contents is $contents", PEAR_LOG_INFO);
					if ( $fdata === NULL)
					{						
						//$log->log("fdata(NULL) Detected is [" . $fdata . "]", PEAR_LOG_INFO);
						array_push($r, "\\null");
					}else{					
						//$log->log("fdata(NULL) Detected is [" . $fdata . "]", PEAR_LOG_INFO);
						array_push($r, tokenise((string)($fdata)));
					} // -- End If/Else -- //
				} // -- End For -- //
				print "ROW" . implode("\\fld", $r) . "\n";				
			} // -- End While -- //
		
		}else {
			print( "ERR" . " " . $dbh->error );			
			//$log->log("ERR" . " " . $dbh->error, PEAR_LOG_ERR);
		} // -- End If/Else -- //
	} // -- End If/Elseif/Else -- //
	$dbh->close();
} // -- End If/Else -- //
?>