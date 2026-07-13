-- ============================================================
-- SmartRoad Dummy Data
-- Generated seed data for testing: 2 admins, 15 mobile users,
-- 60 hazard reports clustered around (2.221341, 102.453102),
-- and 20 maintenance records. Does NOT modify schema.
-- Safe to import on top of an existing smartroad database --
-- foreign keys are resolved via session variables (LAST_INSERT_ID()),
-- not hardcoded IDs, so it does not depend on current auto-increment state.
-- ============================================================

USE smartroad;

-- ------------------------------------------------------------
-- 2 Admin accounts (password: admin123)
-- ------------------------------------------------------------
INSERT INTO admin_users (username, password, full_name) VALUES ('nadia.rahman', '$2b$12$qNhKBg9FTtUZzDHKHg4p5eZPuVl9B5.gCy1TEmdZCx1gvVFGADk4.', 'Nadia binti Abdul Rahman');
INSERT INTO admin_users (username, password, full_name) VALUES ('hafiz.zulkifli', '$2b$12$qNhKBg9FTtUZzDHKHg4p5eZPuVl9B5.gCy1TEmdZCx1gvVFGADk4.', 'Mohd Hafiz bin Zulkifli');

-- ------------------------------------------------------------
-- 15 Mobile app users (password: password123)
-- ------------------------------------------------------------
INSERT INTO users (username, password, full_name, email) VALUES ('haziq.rosli', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Muhammad Haziq bin Rosli', 'haziq.rosli@example.com');
SET @u1 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('ainyusof', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Nurul Ain binti Yusof', 'ainyusof@example.com');
SET @u2 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('amirulzainal', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Amirul Hakim bin Zainal', 'amirulzainal@example.com');
SET @u3 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('aisyahibrahim', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Siti Aisyah binti Ibrahim', 'aisyahibrahim@example.com');
SET @u4 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('farhankamarudin', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Farhan Danial bin Kamarudin', 'farhankamarudin@example.com');
SET @u5 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('izzatirahman', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Nur Izzati binti Rahman', 'izzatirahman@example.com');
SET @u6 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('firdausalias', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Mohd Firdaus bin Alias', 'firdausalias@example.com');
SET @u7 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('sofeahassan', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Aina Sofea binti Hassan', 'sofeahassan@example.com');
SET @u8 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('anuarsalleh', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Khairul Anuar bin Salleh', 'anuarsalleh@example.com');
SET @u9 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('nabilaosman', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Nabila Iman binti Osman', 'nabilaosman@example.com');
SET @u10 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('zulkiflimahmud', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Zulkifli bin Mahmud', 'zulkiflimahmud@example.com');
SET @u11 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('athirahismail', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Wan Nur Athirah binti Wan Ismail', 'athirahismail@example.com');
SET @u12 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('azmanhashim', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Azman bin Hashim', 'azmanhashim@example.com');
SET @u13 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('nurhalizaahmad', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Siti Nurhaliza binti Ahmad', 'nurhalizaahmad@example.com');
SET @u14 := LAST_INSERT_ID();
INSERT INTO users (username, password, full_name, email) VALUES ('rizalyaakob', '$2b$12$tGrGXQInNnMeIMwr4J2apOyEXA6JqNbjmPHIxz6vTlwEfCCvIfajW', 'Rizal bin Yaakob', 'rizalyaakob@example.com');
SET @u15 := LAST_INSERT_ID();

-- ------------------------------------------------------------
-- 60 Hazard reports within ~50m-2km of (2.221341, 102.453102)
-- ------------------------------------------------------------
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u12, 'Accident', 'Vehicle collision near Jalan Solok Air Merbau, debris scattered on the road.', NULL, 2.2082299, 102.4498228, 'Under Investigation', 'Android 14', '2026-05-22 10:31:13');
SET @h1 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u3, 'Broken Traffic Light', 'Broken traffic light at Jalan Sebatu junction, causing minor congestion.', NULL, 2.2227689, 102.4604233, 'Under Investigation', 'Android 13', '2026-05-29 14:19:56');
SET @h2 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u11, 'Pothole', 'Deep pothole causing traffic to slow down on Merlimau Town.', NULL, 2.2223259, 102.4539137, 'New', 'Android 12', '2026-06-29 18:16:23');
SET @h3 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u10, 'Broken Traffic Light', 'Broken traffic light at Jalan Serkam junction, causing minor congestion.', NULL, 2.2131688, 102.4603335, 'Resolved', 'Android 15', '2026-04-15 14:01:42');
SET @h4 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u1, 'Damaged Road Sign', 'Road sign knocked down near Jalan Merlimau junction.', 'hz_6a523bb1b317b1.79958298.png', 2.2325485, 102.4396787, 'Resolved', 'Android 14', '2026-05-14 05:12:47');
SET @h5 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u13, 'Accident', 'Minor accident reported near Jalan Sebatu junction, one lane blocked.', NULL, 2.2214291, 102.4371242, 'New', 'Android 13', '2026-05-17 18:11:02');
SET @h6 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u12, 'Flood', 'Road partially submerged on Merlimau Town following continuous rain.', NULL, 2.2136059, 102.4554184, 'Resolved', 'Android 12', '2026-04-26 12:59:49');
SET @h7 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Damaged Road Sign', 'Damaged road sign along Merlimau Town, visibility affected for drivers.', NULL, 2.2253408, 102.4614247, 'Resolved', 'Android 15', '2026-07-04 14:38:56');
SET @h8 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Jalan Solok Duku.', NULL, 2.2061488, 102.4523694, 'New', 'Android 14', '2026-06-30 08:43:01');
SET @h9 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u9, 'Flood', 'Flood after heavy rain along Jalan Sebatu, water rising near the roadside.', 'hz_6a525c83dad919.23660463.jpg', 2.2231136, 102.4433459, 'Under Investigation', 'Android 13', '2026-04-22 12:52:00');
SET @h10 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u6, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Jalan Serkam.', NULL, 2.2282217, 102.4589360, 'Under Investigation', 'Android 12', '2026-04-25 23:58:50');
SET @h11 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Pothole', 'Large pothole near traffic light along Jalan Solok Air Merbau.', NULL, 2.2268008, 102.4551833, 'New', 'Android 15', '2026-05-24 09:33:50');
SET @h12 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u13, 'Flood', 'Road partially submerged on Jalan Solok Duku following continuous rain.', NULL, 2.2266954, 102.4409347, 'New', 'Android 14', '2026-05-27 11:50:43');
SET @h13 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u3, 'Fallen Tree', 'Tree blocking one lane on Jalan Solok Pengkalan after last night\'s strong winds.', NULL, 2.2326965, 102.4491592, 'New', 'Android 13', '2026-06-20 05:00:30');
SET @h14 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u9, 'Pothole', 'Deep pothole causing traffic to slow down on Jalan Solok Duku.', 'hz_6a5261bcd0f416.33084267.jpg', 2.2232120, 102.4607523, 'Resolved', 'Android 12', '2026-05-07 04:20:55');
SET @h15 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u11, 'Flood', 'Flood after heavy rain along Taman Merlimau Baru, water rising near the roadside.', NULL, 2.2109106, 102.4607872, 'Under Investigation', 'Android 15', '2026-05-16 19:00:22');
SET @h16 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Jalan Kesang.', NULL, 2.2282441, 102.4596718, 'Under Investigation', 'Android 14', '2026-07-08 17:27:05');
SET @h17 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u1, 'Fallen Tree', 'Tree blocking one lane on Jalan Sebatu after last night\'s strong winds.', NULL, 2.2127066, 102.4477364, 'Under Investigation', 'Android 13', '2026-07-07 19:04:54');
SET @h18 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u14, 'Accident', 'Vehicle collision near Taman Merlimau Baru, debris scattered on the road.', NULL, 2.2250402, 102.4506004, 'Under Investigation', 'Android 12', '2026-06-14 12:06:16');
SET @h19 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u9, 'Damaged Road Sign', 'Damaged road sign along Jalan Kesang, visibility affected for drivers.', 'hz_6a51cba7cbd2b2.52403140.png', 2.2160301, 102.4535904, 'Under Investigation', 'Android 15', '2026-07-02 02:58:48');
SET @h20 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u13, 'Broken Traffic Light', 'Traffic light malfunctioning near Jalan Lipat Kajang, drivers confused at junction.', NULL, 2.2110310, 102.4548913, 'New', 'Android 14', '2026-06-01 17:29:37');
SET @h21 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u6, 'Damaged Road Sign', 'Damaged road sign along Jalan Serkam, visibility affected for drivers.', NULL, 2.2202024, 102.4513316, 'Under Investigation', 'Android 13', '2026-06-06 03:27:57');
SET @h22 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u11, 'Broken Traffic Light', 'Traffic light malfunctioning near Merlimau Town, drivers confused at junction.', NULL, 2.2152114, 102.4413628, 'Under Investigation', 'Android 12', '2026-07-05 10:35:09');
SET @h23 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Damaged Road Sign', 'Damaged road sign along Jalan Sebatu, visibility affected for drivers.', NULL, 2.2254721, 102.4482102, 'New', 'Android 15', '2026-06-26 23:43:48');
SET @h24 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u8, 'Broken Traffic Light', 'Traffic light malfunctioning near Jalan Solok Air Merbau, drivers confused at junction.', 'hz_6a523bb1b317b1.79958298.png', 2.2225212, 102.4557617, 'Under Investigation', 'Android 14', '2026-06-21 13:20:19');
SET @h25 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u9, 'Pothole', 'Large pothole near traffic light along Jalan Lipat Kajang.', NULL, 2.2266346, 102.4468077, 'Resolved', 'Android 13', '2026-07-05 08:41:29');
SET @h26 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u2, 'Pothole', 'Deep pothole causing traffic to slow down on Jalan Kesang.', NULL, 2.2312282, 102.4540221, 'Under Investigation', 'Android 12', '2026-04-21 16:51:46');
SET @h27 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Accident', 'Minor accident reported near Jalan Solok Pengkalan junction, one lane blocked.', NULL, 2.2179885, 102.4534055, 'New', 'Android 15', '2026-04-27 05:31:33');
SET @h28 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u7, 'Flood', 'Road partially submerged on Jalan Lipat Kajang following continuous rain.', NULL, 2.2246776, 102.4531472, 'Resolved', 'Android 14', '2026-06-20 05:45:09');
SET @h29 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u12, 'Damaged Road Sign', 'Damaged road sign along Taman Merlimau Baru, visibility affected for drivers.', 'hz_6a525c83dad919.23660463.jpg', 2.2138792, 102.4570254, 'Resolved', 'Android 13', '2026-04-15 15:13:03');
SET @h30 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u5, 'Pothole', 'Deep pothole causing traffic to slow down on Jalan Sebatu.', NULL, 2.2286890, 102.4638927, 'Resolved', 'Android 12', '2026-06-24 21:03:51');
SET @h31 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u6, 'Damaged Road Sign', 'Damaged road sign along Jalan Lipat Kajang, visibility affected for drivers.', NULL, 2.2337257, 102.4580956, 'New', 'Android 15', '2026-07-09 01:30:46');
SET @h32 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u3, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Jalan Solok Air Merbau.', NULL, 2.2269159, 102.4456926, 'Under Investigation', 'Android 14', '2026-07-09 03:02:46');
SET @h33 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u2, 'Accident', 'Minor accident reported near Jalan Lipat Kajang junction, one lane blocked.', NULL, 2.2353170, 102.4595255, 'Resolved', 'Android 13', '2026-05-14 14:43:06');
SET @h34 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Pothole', 'Deep pothole causing traffic to slow down on Taman Merlimau Baru.', 'hz_6a5261bcd0f416.33084267.jpg', 2.2233103, 102.4514776, 'Resolved', 'Android 12', '2026-05-23 09:03:28');
SET @h35 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u10, 'Pothole', 'Large pothole near traffic light along Jalan Merlimau.', NULL, 2.2203035, 102.4515242, 'New', 'Android 15', '2026-05-27 23:34:22');
SET @h36 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Broken Traffic Light', 'Traffic light malfunctioning near Jalan Solok Duku, drivers confused at junction.', NULL, 2.2204910, 102.4491579, 'Under Investigation', 'Android 14', '2026-06-20 12:42:07');
SET @h37 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Pothole', 'Large pothole near traffic light along Jalan Solok Pengkalan.', NULL, 2.2099939, 102.4562297, 'Resolved', 'Android 13', '2026-05-07 19:06:07');
SET @h38 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u2, 'Accident', 'Vehicle collision near Jalan Solok Duku, debris scattered on the road.', NULL, 2.2298271, 102.4530195, 'New', 'Android 12', '2026-07-07 19:11:56');
SET @h39 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Broken Traffic Light', 'Broken traffic light at Taman Merlimau Baru junction, causing minor congestion.', 'hz_6a51cba7cbd2b2.52403140.png', 2.2259995, 102.4510237, 'Resolved', 'Android 15', '2026-07-08 04:28:45');
SET @h40 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u9, 'Flood', 'Road partially submerged on Jalan Merlimau following continuous rain.', NULL, 2.2162993, 102.4551665, 'Under Investigation', 'Android 14', '2026-05-11 21:00:49');
SET @h41 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u1, 'Damaged Road Sign', 'Damaged road sign along Jalan Solok Pengkalan, visibility affected for drivers.', NULL, 2.2112627, 102.4384645, 'New', 'Android 13', '2026-05-15 02:45:36');
SET @h42 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Merlimau Town.', NULL, 2.2346983, 102.4632819, 'Under Investigation', 'Android 12', '2026-07-02 08:32:45');
SET @h43 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u5, 'Accident', 'Minor accident reported near Jalan Kesang junction, one lane blocked.', NULL, 2.2191432, 102.4523378, 'New', 'Android 15', '2026-06-19 00:52:44');
SET @h44 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u14, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Jalan Lipat Kajang.', 'hz_6a523bb1b317b1.79958298.png', 2.2188677, 102.4471268, 'Resolved', 'Android 14', '2026-06-20 11:55:56');
SET @h45 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u2, 'Flood', 'Flood after heavy rain along Jalan Solok Air Merbau, water rising near the roadside.', NULL, 2.2306112, 102.4396527, 'New', 'Android 13', '2026-05-17 23:59:36');
SET @h46 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u3, 'Broken Traffic Light', 'Traffic light malfunctioning near Jalan Kesang, drivers confused at junction.', NULL, 2.2207281, 102.4541609, 'New', 'Android 12', '2026-05-18 00:41:29');
SET @h47 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u7, 'Damaged Road Sign', 'Damaged road sign along Jalan Solok Duku, visibility affected for drivers.', NULL, 2.2085953, 102.4488082, 'New', 'Android 15', '2026-05-24 23:05:55');
SET @h48 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u3, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Taman Merlimau Baru.', NULL, 2.2151760, 102.4372228, 'Resolved', 'Android 14', '2026-05-25 23:30:35');
SET @h49 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u3, 'Flood', 'Flood after heavy rain along Jalan Solok Pengkalan, water rising near the roadside.', 'hz_6a525c83dad919.23660463.jpg', 2.2277126, 102.4616609, 'New', 'Android 13', '2026-07-10 13:16:25');
SET @h50 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u1, 'Pothole', 'Deep pothole causing traffic to slow down on Jalan Serkam.', NULL, 2.2378981, 102.4501776, 'New', 'Android 12', '2026-04-24 11:48:06');
SET @h51 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u13, 'Accident', 'Minor accident reported near Merlimau Town junction, one lane blocked.', NULL, 2.2251946, 102.4560087, 'Resolved', 'Android 15', '2026-05-24 16:09:18');
SET @h52 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Accident', 'Vehicle collision near Jalan Serkam, debris scattered on the road.', NULL, 2.2212835, 102.4355613, 'New', 'Android 14', '2026-04-21 09:04:54');
SET @h53 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u15, 'Accident', 'Minor accident reported near Jalan Merlimau junction, one lane blocked.', NULL, 2.2278878, 102.4367859, 'Under Investigation', 'Android 13', '2026-06-07 07:17:30');
SET @h54 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u7, 'Fallen Tree', 'Fallen tree branch obstructing traffic along Jalan Merlimau.', 'hz_6a5261bcd0f416.33084267.jpg', 2.2247082, 102.4367713, 'Under Investigation', 'Android 12', '2026-05-02 20:48:11');
SET @h55 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u13, 'Broken Traffic Light', 'Broken traffic light at Jalan Merlimau junction, causing minor congestion.', NULL, 2.2197951, 102.4676989, 'Resolved', 'Android 15', '2026-05-12 04:38:55');
SET @h56 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Broken Traffic Light', 'Traffic light malfunctioning near Jalan Solok Pengkalan, drivers confused at junction.', NULL, 2.2202313, 102.4533089, 'Resolved', 'Android 14', '2026-05-01 18:38:57');
SET @h57 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u4, 'Flood', 'Flood after heavy rain along Jalan Kesang, water rising near the roadside.', NULL, 2.2229717, 102.4475300, 'Under Investigation', 'Android 13', '2026-06-24 02:33:53');
SET @h58 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u2, 'Flood', 'Road partially submerged on Jalan Serkam following continuous rain.', NULL, 2.2200115, 102.4604252, 'Resolved', 'Android 12', '2026-04-18 08:10:12');
SET @h59 := LAST_INSERT_ID();
INSERT INTO hazard_reports (user_id, hazard_type, description, photo, latitude, longitude, status, user_agent, reported_at) VALUES (@u14, 'Damaged Road Sign', 'Damaged road sign along Jalan Solok Air Merbau, visibility affected for drivers.', 'hz_6a51cba7cbd2b2.52403140.png', 2.2118901, 102.4600029, 'Resolved', 'Android 15', '2026-05-26 18:30:11');
SET @h60 := LAST_INSERT_ID();

-- ------------------------------------------------------------
-- 20 Maintenance records (only for Resolved / Under Investigation reports)
-- ------------------------------------------------------------
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h4, 'Majlis Perbandaran Melaka Tengah - Unit Penyelenggaraan Jalan', 'Traffic light repaired and function tested.', '2026-04-24', '2026-04-26');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h7, 'JKR Melaka - Skuad Baik Pulih Jalan', 'Drainage cleared and water level monitored until subsided.', '2026-04-30', '2026-05-04');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h10, 'Kontraktor Awam Merlimau Sdn Bhd', 'Drainage cleared and water level monitored until subsided.', '2026-04-29', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h11, 'Pasukan Kecemasan Jalan Raya Daerah Melaka Tengah', 'Fallen tree removed and road reopened to traffic.', '2026-04-28', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h15, 'Unit Penyelenggaraan Infrastruktur MPMT', 'Pothole patched and road surface resurfaced.', '2026-05-17', '2026-05-22');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h16, 'Majlis Perbandaran Melaka Tengah - Unit Penyelenggaraan Jalan', 'Drainage cleared and water level monitored until subsided.', '2026-05-21', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h19, 'JKR Melaka - Skuad Baik Pulih Jalan', 'Debris cleared and road markings repainted.', '2026-06-21', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h20, 'Kontraktor Awam Merlimau Sdn Bhd', 'Road sign replaced with new signage.', '2026-07-11', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h27, 'Pasukan Kecemasan Jalan Raya Daerah Melaka Tengah', 'Pothole patched and road surface resurfaced.', '2026-04-22', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h30, 'Unit Penyelenggaraan Infrastruktur MPMT', 'Road sign replaced with new signage.', '2026-04-20', '2026-04-23');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h33, 'Majlis Perbandaran Melaka Tengah - Unit Penyelenggaraan Jalan', 'Fallen tree removed and road reopened to traffic.', '2026-07-13', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h35, 'JKR Melaka - Skuad Baik Pulih Jalan', 'Pothole patched and road surface resurfaced.', '2026-05-30', '2026-06-04');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h37, 'Kontraktor Awam Merlimau Sdn Bhd', 'Traffic light repaired and function tested.', '2026-06-30', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h40, 'Pasukan Kecemasan Jalan Raya Daerah Melaka Tengah', 'Traffic light repaired and function tested.', '2026-07-14', '2026-07-18');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h41, 'Unit Penyelenggaraan Infrastruktur MPMT', 'Drainage cleared and water level monitored until subsided.', '2026-05-19', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h45, 'Majlis Perbandaran Melaka Tengah - Unit Penyelenggaraan Jalan', 'Fallen tree removed and road reopened to traffic.', '2026-06-28', '2026-06-30');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h49, 'JKR Melaka - Skuad Baik Pulih Jalan', 'Fallen tree removed and road reopened to traffic.', '2026-06-03', '2026-06-07');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h56, 'Kontraktor Awam Merlimau Sdn Bhd', 'Traffic light repaired and function tested.', '2026-05-15', '2026-05-16');
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h58, 'Pasukan Kecemasan Jalan Raya Daerah Melaka Tengah', 'Drainage cleared and water level monitored until subsided.', '2026-06-29', NULL);
INSERT INTO maintenance_records (hazard_report_id, maintenance_team, maintenance_notes, repair_date, completed_date) VALUES (@h60, 'Unit Penyelenggaraan Infrastruktur MPMT', 'Road sign replaced with new signage.', '2026-06-04', '2026-06-09');

