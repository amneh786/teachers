<?php
header("Content-Type: application/json");

$pdo = new PDO("mysql:host=localhost;dbname=school1;charset=utf8mb4", "root", "");
$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);



$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['class_id'], $data['subject_id'], $data['teacher_id'], $data['title'], $data['due_date'])) {
    echo json_encode(["error" => "Missing fields"]);
    exit;
}

$stmt = $pdo->prepare("
    INSERT INTO assignment (class_id, subject_id, teacher_id, title, description, due_date)
    VALUES (:class_id, :subject_id, :teacher_id, :title, :description, :due_date)
");

$stmt->execute([
    'class_id' => $data['class_id'],
    'subject_id' => $data['subject_id'],
    'teacher_id' => $data['teacher_id'],
    'title' => $data['title'],
    'description' => $data['description'] ?? null,
    'due_date' => $data['due_date']
]);

echo json_encode(["success" => true]);
