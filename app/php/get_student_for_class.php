<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;
$class_name = $_GET['class_name'] ?? null;
$type = $_GET['type'] ?? null; // نوع الامتحان

if (!$user_id || !is_numeric($user_id) || !$class_name || !$type) {
    echo json_encode(["status" => "error", "message" => "Missing or invalid user_id, class_name, or type"]);
    exit;
}

// Get teacher_id from user_id
$teacherStmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$teacherStmt->bind_param("i", $user_id);
$teacherStmt->execute();
$teacherResult = $teacherStmt->get_result();

if ($teacherResult->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "No teacher found for this user"]);
    exit;
}

$teacherRow = $teacherResult->fetch_assoc();
$teacher_id = $teacherRow['id'];

// Get students + their marks (if exists) for that class
$query = "
    SELECT 
        s.id AS student_id, 
        u.name AS student_name, 
        COALESCE(m.mark, '') AS mark
    FROM student s
    JOIN user u ON s.user_id = u.id
    JOIN class c ON s.class_id = c.id
    JOIN schedule sch ON c.id = sch.class_id
    LEFT JOIN marks m 
        ON m.student_id = s.id AND m.class_id = c.id AND m.teacher_id = ? AND m.type = ?
    WHERE c.name = ? AND sch.teacher_id = ?
";

$stmt = $conn->prepare($query);
$stmt->bind_param("issi", $teacher_id, $type, $class_name, $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$students = [];
while ($row = $result->fetch_assoc()) {
    $students[] = $row;
}

echo json_encode(["status" => "success", "students" => $students]);
$conn->close();
?>
