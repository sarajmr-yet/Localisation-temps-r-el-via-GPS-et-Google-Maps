<?php
header('Content-Type: application/json; charset=utf-8');

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    http_response_code(405);
    echo json_encode(["ok" => false, "message" => "Methode non autorisee"]);
    exit;
}

require_once __DIR__ . '/repository/GeoPointRepository.php';
require_once __DIR__ . '/model/GeoPoint.php';

$lat      = $_POST['latitude']  ?? null;
$lng      = $_POST['longitude'] ?? null;
$dat      = $_POST['date']      ?? null;
$deviceId = $_POST['deviceId']  ?? null;
$clientIp = $_SERVER['REMOTE_ADDR'];

if (!$lat || !$lng || !$dat || !$deviceId) {
    http_response_code(400);
    echo json_encode([
        "ok"      => false,
        "message" => "Parametres manquants",
        "ip"      => $clientIp
    ]);
    exit;
}

try {
    $repo  = new GeoPointRepository();
    $point = new GeoPoint(null, $lat, $lng, $dat, $deviceId);
    $newId = $repo->insert($point);

    echo json_encode([
        "ok" => true,
        "id" => $newId,
        "ip" => $clientIp
    ]);
} catch (Exception $e) {
    http_response_code(500);
    echo json_encode(["ok" => false, "message" => $e->getMessage()]);
}
