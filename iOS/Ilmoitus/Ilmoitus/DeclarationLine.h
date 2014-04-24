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
@property (strong, nonatomic) NSString *subtype;
@property (nonatomic) float cost;

- (instancetype)initWithDate:(NSString *)date SubType:(NSString *)subtype Cost:(float)cost;

@end
