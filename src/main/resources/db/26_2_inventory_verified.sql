-- Apply after PR #521 status migration, before this API deployment.
ALTER TABLE umbrella ADD COLUMN inventory_verified_at DATETIME(6) NULL;
