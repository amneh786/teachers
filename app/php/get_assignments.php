<?php
header("Access-Control-Allow-Origin: *");
header("Content-Type: application/json; charset=UTF-8");

$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;

if (!$user_id || !is_numeric($user_id)) {
    echo json_encode(["error" => "Missing or invalid user_id"]);
    exit;
}

$teacher_stmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacher_stmt->bind_param("i", $user_id);
$teacher_stmt->execute();
$teacher_stmt->bind_result($teacher_id);
if (!$teacher_stmt->fetch()) {
    echo json_encode(["error" => "Teacher not found"]);
    exit;
}
$teacher_stmt->close();

$query = "
    SELECT a.title AS assignment_title, c.name AS class_name, a.due_date
    FROM assignment a
    JOIN class c ON a.class_id = c.id
    WHERE a.teacher_id = ?
";
$stmt = $conn->prepare($query);
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$assignments = [];
while ($row = $result->fetch_assoc()) {
    $assignments[] = $row;
}

echo json_encode($assignments);
$conn->close();
?>
