-- Apply before deploying campaign and banner APIs. Campaign defaults to disabled.
CREATE TABLE IF NOT EXISTS campaign_settings (
  id BIGINT NOT NULL PRIMARY KEY,
  campaign_key VARCHAR(255),
  enabled BIT(1) NOT NULL,
  return_deadline_days INT NOT NULL,
  title VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS stamp_ledger (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  amount INT NOT NULL,
  award_key VARCHAR(120),
  awarded_at DATETIME(6),
  campaign_key VARCHAR(255),
  history_id BIGINT,
  mission VARCHAR(255),
  user_id BIGINT NOT NULL,
  UNIQUE KEY UK6qvyixjiukq1ealqn5v5huc4s (award_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS campaign_survey (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  campaign_key VARCHAR(255),
  ease_of_use INT NOT NULL,
  feedback VARCHAR(1000),
  satisfaction INT NOT NULL,
  submitted_at DATETIME(6),
  user_id BIGINT,
  UNIQUE KEY UKbq6819i586dvsk5a3o2gu18fv (campaign_key,user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS banner (
  id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  deleted BIT(1) NOT NULL,
  display_order INT NOT NULL,
  enabled BIT(1) NOT NULL,
  ends_at DATETIME(6),
  image_url VARCHAR(1000),
  starts_at DATETIME(6),
  target_url VARCHAR(1000),
  title VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

