<?php
header("Content-Type: application/json");
$conn = new mysqli("localhost", "root", "", "school1");

$user_id = $_GET['user_id'] ?? null;
if (!$user_id || !is_numeric($user_id)) {
    echo json_encode(["status" => "error", "message" => "Missing or invalid user_id"]);
    exit;
}

$stmt = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();
if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Teacher not found"]);
    exit;
}
$teacher_row = $result->fetch_assoc();
$teacher_id = $teacher_row['id'];
$stmt->close();

$stmt = $conn->prepare("SELECT class_id FROM class_homeroom_teacher WHERE teacher_id = ?");
$stmt->bind_param("i", $teacher_id);
$stmt->execute();
$result = $stmt->get_result();

$class_ids = [];
while ($row = $result->fetch_assoc()) {
    $class_ids[] = $row['class_id'];
}
$stmt->close();

if (empty($class_ids)) {
    echo json_encode(["status" => "error", "message" => "No homeroom class found for this teacher"]);
    exit;
}

$placeholders = implode(',', array_fill(0, count($class_ids), '?'));
$query = "
    SELECT s.id, u.name, s.class_id
    FROM student s
    JOIN user u ON s.user_id = u.id
    WHERE s.class_id IN ($placeholders)
";

$stmt = $conn->prepare($query);
$params = array_merge([str_repeat('i', count($class_ids))], $class_ids);
call_user_func_array([$stmt, 'bind_param'], refValues($params));

$stmt->execute();
$result = $stmt->get_result();

$data = [];
while ($row = $result->fetch_assoc()) {
    $data[] = $row;
}

echo json_encode(["status" => "success", "data" => $data]);
$conn->close();

function refValues($arr) {
    $refs = [];
    foreach ($arr as $key => $value) {
        $refs[$key] = &$arr[$key];
    }
    return $refs;
}
?>
