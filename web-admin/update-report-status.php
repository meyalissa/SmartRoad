<?php

require_once __DIR__ . '/db.php';
require_once 'auth.php';

header('Content-Type: application/json');

try {

    $data = json_decode(
        file_get_contents("php://input"),
        true
    );

    $id = $data['id'];
    $status = $data['status'];

    $allowed = [
        "New",
        "Under Investigation",
        "Resolved"
    ];

    if (!in_array($status, $allowed)) {

        echo json_encode([
            "success" => false,
            "message" => "Invalid status"
        ]);

        exit;
    }


    $stmt = $pdo->prepare(
        "UPDATE hazard_reports
         SET status = ?
         WHERE id = ?"
    );


    $stmt->execute([
        $status,
        $id
    ]);


    echo json_encode([
        "success" => true
    ]);


} catch (PDOException $e) {

    echo json_encode([
        "success" => false,
        "message" => $e->getMessage()
    ]);

}

?>