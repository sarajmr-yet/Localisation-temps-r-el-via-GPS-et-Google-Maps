<?php
class DatabaseHelper {
    private $pdo;

    public function __construct() {
        $host = 'localhost';
        $base = 'localisation';
        $user = 'root';
        $pass = '';

        try {
            $dsn = "mysql:host=$host;dbname=$base;charset=utf8";
            $this->pdo = new PDO($dsn, $user, $pass, [
                PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
                PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC
            ]);
        } catch (PDOException $e) {
            http_response_code(500);
            die(json_encode(["ok" => false, "error" => $e->getMessage()]));
        }
    }

    public function getPdo() {
        return $this->pdo;
    }
}
