"""'fix'

Revision ID: 6b5b9c1e812b
Revises: e6494aa55c77
Create Date: 2023-03-05 18:07:23.904796

"""
import sqlalchemy as sa
from alembic import op

# revision identifiers, used by Alembic.
revision = "6b5b9c1e812b"
down_revision = "e6494aa55c77"
branch_labels = None
depends_on = None


def upgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index("ix_users_username", table_name="users")
    op.drop_constraint("users_email_key", "users", type_="unique")
    op.create_index(op.f("ix_users_email"), "users", ["email"], unique=True)
    # ### end Alembic commands ###


def downgrade() -> None:
    # ### commands auto generated by Alembic - please adjust! ###
    op.drop_index(op.f("ix_users_email"), table_name="users")
    op.create_unique_constraint("users_email_key", "users", ["email"])
    op.create_index("ix_users_username", "users", ["username"], unique=False)
    # ### end Alembic commands ###
