<?php
header("Content-Type: application/json");
ini_set('display_errors', 1);
error_reporting(E_ALL);

$conn = new mysqli("localhost", "root", "", "school1");

$title = $_REQUEST["title"] ?? null;
$description = $_REQUEST["description"] ?? null;
$due_date = $_REQUEST["due_date"] ?? null;
$class_name = $_REQUEST["class_name"] ?? null;
$subject_name = $_REQUEST["subject_name"] ?? null;
$user_id = $_REQUEST["user_id"] ?? null;

if (!$title || !$description || !$due_date || !$class_name || !$subject_name || !$user_id) {
    echo json_encode(["status" => "error", "message" => "Missing fields"]);
    exit;
}

$teacher_stmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacher_stmt->bind_param("i", $user_id);
$teacher_stmt->execute();
$teacher_stmt->bind_result($teacher_id);
if (!$teacher_stmt->fetch()) {
    echo json_encode(["status" => "error", "message" => "Invalid user_id (no teacher found)"]);
    exit;
}
$teacher_stmt->close();

$class_stmt = $conn->prepare("SELECT id FROM class WHERE name = ?");
$class_stmt->bind_param("s", $class_name);
$class_stmt->execute();
$class_stmt->bind_result($class_id);
if (!$class_stmt->fetch()) {
    echo json_encode(["status" => "error", "message" => "Invalid class name"]);
    exit;
}
$class_stmt->close();

$subject_stmt = $conn->prepare("SELECT id FROM subject WHERE name = ?");
$subject_stmt->bind_param("s", $subject_name);
$subject_stmt->execute();
$subject_stmt->bind_result($subject_id);
if (!$subject_stmt->fetch()) {
    echo json_encode(["status" => "error", "message" => "Invalid subject name"]);
    exit;
}
$subject_stmt->close();

$insert_stmt = $conn->prepare("INSERT INTO assignment (title, description, due_date, class_id, subject_id, teacher_id) VALUES (?, ?, ?, ?, ?, ?)");
$insert_stmt->bind_param("sssiii", $title, $description, $due_date, $class_id, $subject_id, $teacher_id);

if ($insert_stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Assignment added successfully"]);
} else {
    echo json_encode(["status" => "error", "message" => $insert_stmt->error]);
}

$insert_stmt->close();
$conn->close();
?>
