
CREATE TABLE TRIGGER(
	ID					BIGINT			NOT NULL PRIMARY KEY,	-- �g���K�[ID(PK)
	NAME				VARCHAR(255),							-- �g���K�[��
	DESCRIPTION			VARCHAR(255),							-- ����
	EXECUTED			BOOLEAN			NOT NULL DEFAULT 0,		-- ���s���(���s��=true,��~��=false)
	EXEC_TYPE			CHAR(2)			NOT NULL,				-- TT = Timed, CT = Cron, DT = Delay, NT = NonDelay
	DELAY_TIME			BIGINT,									-- DT���̒x������			
	CRON_EXPRESSION		VARCHAR(255),							-- CT����CRON�R�}���h
	START_TIME			TIMESTAMP,								-- TT���̊J�n�\�莞�� (CT����CRON_EXPRESSION)
	END_TIME			TIMESTAMP,								-- TT���̏I���\�莞��
	VESRION_NO			DECIMAL
);

CREATE TABLE CRON_EXPRESSION(
	ID				BIGINT				NOT NULL,				-- �g���K�[ID(PK)
	START_TIME		TIMESTAMP			NOT NULL,				-- �J�n�\�莞��
	VESRION_NO		DECIMAL
	FOREIGN KEY(ID) REFERENCES TRIGGER(ID),
	PRIMARY KEY(ID, START_TIME)
);

CREATE TABLE TRIGGER_LOG(
	ID					BIGINT			NOT NULL PRIMARY KEY,	-- �g���K�[ID(PK)
	NAME				VARCHAR(255),							-- �g���K�[��
	DESCRIPTION			VARCHAR(255),							-- ����
	EXECUTED			BOOLEAN			NOT NULL DEFAULT 0,		-- ���s���(���s��=true,��~��=false)
	EXEC_TYPE			CHAR(2)			NOT NULL,				-- TT = Timed, CT = Cron, DT = Delay, NT = NonDelay
	DELAY_TIME			BIGINT,									-- DT���̒x������			
	CRON_EXPRESSION		VARCHAR(255),							-- CT����CRON�R�}���h
	START_TIME			TIMESTAMP,								-- TT���̊J�n�\�莞�� (CT����CRON_EXPRESSION)
	END_TIME			TIMESTAMP,								-- TT���̏I���\�莞��
	VESRION_NO			DECIMAL,
	PRIMARY KEY(ID, START_TIME)
);