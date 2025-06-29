package com.hien.le.dkvfinder.core.model.data

import com.google.gson.annotations.SerializedName

data class Poi(
    @SerializedName("ID") val id: Int,
    @SerializedName("UUID") val uuid: String?,
    @SerializedName("UserComments") val userComments: List<UserComment> = emptyList(),
    @SerializedName("MediaItems") val mediaItems: List<MediaItem> = emptyList(),
    @SerializedName("IsRecentlyVerified") val isRecentlyVerified: Boolean?,
    @SerializedName("DateLastVerified") val dateLastVerified: String?,
    @SerializedName("ParentChargePointID") val parentChargePointId: Int?,
    @SerializedName("DataProviderID") val dataProviderId: Int?,
    @SerializedName("DataProvidersReference") val dataProvidersReference: String?,
    @SerializedName("OperatorID") val operatorId: Int?,
    @SerializedName("OperatorsReference") val operatorsReference: String?,
    @SerializedName("UsageTypeID") val usageTypeId: Int?,
    @SerializedName("UsageCost") val usageCost: String?,
    @SerializedName("AddressInfo") val addressInfo: AddressInfo?,
    @SerializedName("Connections") val connections: List<Connection> = emptyList(),
    @SerializedName("NumberOfPoints") val numberOfPoints: Int?,
    @SerializedName("GeneralComments") val generalComments: String?,
    @SerializedName("DatePlanned") val datePlanned: String?,
    @SerializedName("DateLastConfirmed") val dateLastConfirmed: String?,
    @SerializedName("StatusTypeID") val statusTypeId: Int?,
    @SerializedName("DateLastStatusUpdate") val dateLastStatusUpdate: String?,
    @SerializedName("MetadataValues") val metadataValues: List<Any?> = emptyList(),
    @SerializedName("DataQualityLevel") val dataQualityLevel: Int?,
    @SerializedName("DateCreated") val dateCreated: String?,
    @SerializedName("SubmissionStatusTypeID") val submissionStatusTypeId: Int?,
    @SerializedName("DataProvider") val dataProvider: DataProvider?,
    @SerializedName("OperatorInfo") val operatorInfo: OperatorInfo?,
    @SerializedName("UsageType") val usageType: UsageType?,
    @SerializedName("StatusType") val statusType: StatusType?,
    @SerializedName("SubmissionStatus") val submissionStatus: SubmissionStatus?,
)

data class UserComment(
    @SerializedName("ID") val id: String?,
    @SerializedName("ChargePointID") val chargePointId: Int?,
    @SerializedName("CommentTypeID") val commentTypeId: Int?,
    @SerializedName("CommentType") val commentType: CommentType?,
    @SerializedName("UserName") val userName: String?,
    @SerializedName("Comment") val comment: String?,
    @SerializedName("RelatedURL") val relatedUrl: String?,
    @SerializedName("DateCreated") val dateCreated: String?,
    @SerializedName("User") val user: User?,
    @SerializedName("CheckinStatusTypeID") val checkinStatusTypeId: Int?,
    @SerializedName("CheckinStatusType") val checkinStatusType: CheckinStatusType?,
)

data class CommentType(
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class CheckinStatusType(
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
    @SerializedName("IsAutomatedCheckin") val isAutomatedCheckin: Boolean?,
    @SerializedName("IsPositive") val isPositive: Boolean?,
)

data class User(
    @SerializedName("ID") val id: Int?,
    @SerializedName("Username") val username: String?,
    @SerializedName("ReputationPoints") val reputationPoints: Int?,
    @SerializedName("ProfileImageURL") val profileImageUrl: String?,
)

data class MediaItem(
    @SerializedName("ID") val id: String?,
    @SerializedName("ChargePointID") val chargePointId: String?,
    @SerializedName("ItemURL") val itemUrl: String?,
    @SerializedName("ItemThumbnailURL") val itemThumbnailUrl: String?,
    @SerializedName("Comment") val comment: String?,
    @SerializedName("IsEnabled") val isEnabled: Boolean?,
    @SerializedName("IsVideo") val isVideo: Boolean?,
    @SerializedName("IsFeaturedItem") val isFeaturedItem: Boolean?,
    @SerializedName("IsExternalResource") val isExternalResource: Boolean?,
    @SerializedName("User") val user: User?,
    @SerializedName("DateCreated") val dateCreated: String?,
)

data class AddressInfo(
    @SerializedName("ID") val id: Int?,
    @SerializedName("AddressLine1") val addressLine1: String?,
    @SerializedName("AddressLine2") val addressLine2: String?,
    @SerializedName("Town") val town: String?,
    @SerializedName("StateOrProvince") val stateOrProvince: String?,
    @SerializedName("Postcode") val postcode: String?,
    @SerializedName("CountryID") val countryId: Int?,
    @SerializedName("Country") val country: Country?,
    @SerializedName("Latitude") val latitude: Double?,
    @SerializedName("Longitude") val longitude: Double?,
    @SerializedName("ContactTelephone1") val contactTelephone1: String?,
    @SerializedName("ContactTelephone2") val contactTelephone2: String?,
    @SerializedName("ContactEmail") val contactEmail: String?,
    @SerializedName("AccessComments") val accessComments: String?,
    @SerializedName("RelatedURL") val relatedUrl: String?,
    @SerializedName("Distance") val distance: Double?,
    @SerializedName("DistanceUnit") val distanceUnit: Int?,
    @SerializedName("Title") val title: String?,
)

data class Country(
    @SerializedName("ID") val id: Int?,
    @SerializedName("ISOCode") val isoCode: String?,
    @SerializedName("ContinentCode") val continentCode: String?,
    @SerializedName("Title") val title: String?,
)

data class Connection(
    @SerializedName("ID") val id: Int?,
    @SerializedName("ConnectionTypeID") val connectionTypeId: Int?,
    @SerializedName("ConnectionType") val connectionType: ConnectionType?,
    @SerializedName("Reference") val reference: String?,
    @SerializedName("StatusTypeID") val statusTypeId: Int?,
    @SerializedName("StatusType") val statusType: StatusType?,
    @SerializedName("LevelID") val levelId: Int?,
    @SerializedName("Level") val level: Level?,
    @SerializedName("Amps") val amps: Int?,
    @SerializedName("Voltage") val voltage: Int?,
    @SerializedName("PowerKW") val powerKW: Double?,
    @SerializedName("CurrentTypeID") val currentTypeId: Int?,
    @SerializedName("CurrentType") val currentType: CurrentType?,
    @SerializedName("Quantity") val quantity: Int?,
    @SerializedName("Comments") val comments: String?,
)

data class ConnectionType(
    @SerializedName("FormalName") val formalName: String?,
    @SerializedName("IsDiscontinued") val isDiscontinued: Boolean?,
    @SerializedName("IsObsolete") val isObsolete: Boolean?,
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class StatusType(
    @SerializedName("IsOperational") val isOperational: Boolean?,
    @SerializedName("IsUserSelectable") val isUserSelectable: Boolean?,
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class Level(
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
    @SerializedName("Comments") val comments: String?,
    @SerializedName("IsFastChargeCapable") val isFastChargeCapable: Boolean?,
)

data class CurrentType(
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class DataProvider(
    @SerializedName("WebsiteURL") val websiteUrl: String?,
    @SerializedName("Comments") val comments: String?,
    @SerializedName("DataProviderStatusType") val dataProviderStatusType: DataProviderStatusType?,
    @SerializedName("IsRestrictedEdit") val isRestrictedEdit: Boolean?,
    @SerializedName("IsOpenDataLicensed") val isOpenDataLicensed: Boolean?,
    @SerializedName("IsApprovedImport") val isApprovedImport: Boolean?,
    @SerializedName("License") val license: String?,
    @SerializedName("DateLastImported") val dateLastImported: String?,
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class DataProviderStatusType(
    @SerializedName("IsProviderEnabled") val isProviderEnabled: Boolean?,
    @SerializedName("ID") val id: List<Int>?,
    @SerializedName("description") val description: List<String>?,
)

data class OperatorInfo(
    @SerializedName("WebsiteURL") val websiteUrl: String?,
    @SerializedName("Comments") val comments: String?,
    @SerializedName("PhonePrimaryContact") val phonePrimaryContact: String?,
    @SerializedName("PhoneSecondaryContact") val phoneSecondaryContact: String?,
    @SerializedName("IsPrivateIndividual") val isPrivateIndividual: Boolean?,
    @SerializedName("AddressInfo") val addressInfo: AddressInfo?,
    @SerializedName("BookingURL") val bookingUrl: String?,
    @SerializedName("ContactEmail") val contactEmail: String?,
    @SerializedName("FaultReportEmail") val faultReportEmail: String?,
    @SerializedName("IsRestrictedEdit") val isRestrictedEdit: Boolean?,
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class UsageType(
    @SerializedName("IsPayAtLocation") val isPayAtLocation: Boolean?,
    @SerializedName("IsMembershipRequired") val isMembershipRequired: Boolean?,
    @SerializedName("IsAccessKeyRequired") val isAccessKeyRequired: Boolean?,
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
)

data class SubmissionStatus(
    @SerializedName("ID") val id: Int?,
    @SerializedName("Title") val title: String?,
    @SerializedName("IsLive") val isLive: Boolean?,
)
