# The pinned Conpot source image currently omits pkg_resources from its Python
# 3.12 runtime even though pyfilesystem2 imports it. Keep the upstream commit
# immutable and layer only the missing, version-pinned runtime dependency.
FROM atlas-conpot-upstream:32fc03b
USER root
RUN pip install --no-cache-dir setuptools==80.9.0
USER conpot
