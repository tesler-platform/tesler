select
  1 as id,
  'Название содержит "Банк"' as drilldown,
  'screen/doc/view/docDrilldownFilters/legalResidentVanilla?filters='
    || sqlbc_utils_pkg.to_base64 (
      '{"legalResidentVanilla": "legalPersonName.contains=Банк"}'
    ) as drilldown_url
from dual
union all
select
  2 as id,
  'Название содержит "АО" или "ООО"' as drilldown,
  'screen/doc/view/docDrilldownFilters/legalResidentVanilla?filters='
    || sqlbc_utils_pkg.to_base64 (
      '{"legalResidentVanilla": "legalPersonName.containsOneOf=[\"АО\", \"ООО\"]"}'
    ) as drilldown_url
from dual
union all
select
  3 as id,
  'ИНН содержит 1 и КПП содержит 44' as drilldown,
  'screen/doc/view/docDrilldownFilters/legalResidentVanilla?filters='
    || sqlbc_utils_pkg.to_base64 (
      '{"legalResidentVanilla": "inn.contains=1&kpp.contains.КПП=44"}'
    ) as drilldown_url
from dual