<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
error_reporting(E_ALL);

$servername = "localhost";
$username = "root";
$password = "";
$dbname = "school1";

$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "Connection failed"]);
    exit;
}

$email = $_GET['email'] ?? '';
$password = $_GET['password'] ?? '';

if ($email === '' || $password === '') {
    echo json_encode(["status" => "error", "message" => "Missing fields"]);
    exit;
}

$stmt = $conn->prepare("SELECT id, role, name FROM user WHERE email = ? AND password = ?");
$stmt->bind_param("ss", $email, $password);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Invalid credentials"]);
    exit;
}

$user = $result->fetch_assoc();
$user_id = $user['id'];
$role = $user['role'];
$name = $user['name'];

if ($role !== 'teacher') {
    echo json_encode([
        "status" => "success",
        "user_id" => $user_id,
        "role" => $role,
        "name" => $name,
        "homeroom" => false
    ]);
    exit;
}

$stmt2 = $conn->prepare("SELECT id FROM teacher WHERE user_id = ?");
$stmt2->bind_param("i", $user_id);
$stmt2->execute();
$res2 = $stmt2->get_result();

if ($res2->num_rows === 0) {
    echo json_encode(["status" => "error", "message" => "Teacher not found"]);
    exit;
}

$teacher = $res2->fetch_assoc();
$teacher_id = $teacher['id'];

$stmt3 = $conn->prepare("SELECT * FROM class_homeroom_teacher WHERE teacher_id = ?");
$stmt3->bind_param("i", $teacher_id);
$stmt3->execute();
$res3 = $stmt3->get_result();
$isHomeroom = $res3->num_rows > 0;

echo json_encode([
    "status" => "success",
    "user_id" => $user_id,
    "teacher_id" => $teacher_id,
    "role" => $role,
    "name" => $name,
    "homeroom" => $isHomeroom
]);
?>
