//
//  DeclarationLine.h
//  Ilmoitus
//
//  Created by Sjors Boom on 24/04/14.
//  Copyright (c) 2014 42IN12EWa. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface DeclarationLine : NSObject

@property (strong, nonatomic) NSString *date;
@property (nonatomic) float cost;
// ID of the subtype for this declarationline
@property (nonatomic) int64_t subtype;

- (instancetype)initWithDate:(NSString *)date SubType:(NSString *)subtype Cost:(float)cost;

@end