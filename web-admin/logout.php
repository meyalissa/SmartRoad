<?php
/**
 * SmartRoad Admin — Ends the admin session and returns to the login page.
 */

session_start();
$_SESSION = [];
session_destroy();
header('Location: login.php');
exit;